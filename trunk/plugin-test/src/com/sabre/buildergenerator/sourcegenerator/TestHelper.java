/**
 * Copyright (c) 2009-2010 fluent-builder-generator for Eclipse commiters.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Sabre Polska sp. z o.o. - initial implementation during Hackday
 */

package com.sabre.buildergenerator.sourcegenerator;

import java.io.IOException;
import java.io.StringBufferInputStream;
import java.io.Writer;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.IStreamListener;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStreamMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.AbstractVMInstall;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMInstallType;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.ui.PreferenceConstants;

@SuppressWarnings("deprecation")
public class TestHelper {
    private static final String JAVA_EXTENSION = "java";

    /**
     * @param projectName name of the project
     * @param sourcePath e.g. "src"
     * @param javaVMVersion e.g. JavaCore.VERSION_1_6; null indicates default
     * @param targetPlatform e.g. JavaCore.VERSION_1_5; must not be null
     * @throws CoreException error
     * @throws JavaModelException error
     */
    @SuppressWarnings("unchecked")
    public static IJavaProject createJavaProject(String projectName, String sourcePath, String javaVMVersion, String targetPlatform)
            throws CoreException, JavaModelException {
        // create project
        IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
        project.create(null);
        project.open(null);

        // create source folder
        IPath srcPath = new Path(sourcePath);
        IFolder srcFolder = project.getFolder(srcPath);
        srcFolder.create(true, true, null);

        // class path
        IClasspathEntry sourceEntry = JavaCore.newSourceEntry(project.getFullPath().append(srcPath));

        IClasspathEntry[] jreLibrary = null;
        if (javaVMVersion != null) {
            vmType: for (IVMInstallType vmInstallType : JavaRuntime.getVMInstallTypes()) {
                for (IVMInstall vmInstall : vmInstallType.getVMInstalls()) {
                    if (javaVMVersion.equals(((AbstractVMInstall)vmInstall).getJavaVersion())) {
                        IPath containerPath = new Path(JavaRuntime.JRE_CONTAINER);
                        IPath vmPath = containerPath.append(vmInstall.getVMInstallType().getId()).append(vmInstall.getName());
                        jreLibrary = new IClasspathEntry[]{JavaCore.newContainerEntry(vmPath)};
                        break vmType;
                    }
                }
            }
        }
        if (jreLibrary == null) {
            jreLibrary = PreferenceConstants.getDefaultJRELibrary();
        }

        // create java project
        IJavaProject javaProject = JavaCore.create(project);
        IProjectDescription description = project.getDescription();
        String[] natureIds = description.getNatureIds();
        String[] newNatureIds = new String[natureIds.length + 1];
        System.arraycopy(natureIds, 0, newNatureIds, 0, natureIds.length);
        newNatureIds[newNatureIds.length - 1] = JavaCore.NATURE_ID;
        description.setNatureIds(newNatureIds);
        project.setDescription(description, null);

        // create binary folder
        String binName = PreferenceConstants.getPreferenceStore().getString(PreferenceConstants.SRCBIN_BINNAME);
        IFolder binFolder = project.getFolder(binName);
        binFolder.create(IResource.FORCE | IResource.DERIVED, true, null);
        binFolder.setDerived(true);

        project.refreshLocal(IResource.DEPTH_INFINITE, null);

        // set project class path
        javaProject.setRawClasspath(merge(jreLibrary, new IClasspathEntry[]{sourceEntry}), binFolder.getFullPath(), null);

        // set options
        Map<String, String> options = javaProject.getOptions(true);
        options.put(JavaCore.COMPILER_COMPLIANCE, targetPlatform);
        options.put(JavaCore.COMPILER_SOURCE, targetPlatform);
        options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, targetPlatform);
        options.put(JavaCore.COMPILER_PB_ASSERT_IDENTIFIER, JavaCore.ERROR);
        options.put(JavaCore.COMPILER_PB_ENUM_IDENTIFIER, JavaCore.ERROR);
        javaProject.setOptions(options);

        return javaProject;
    }

    public static void deleteJavaProject(IJavaProject javaProject) throws CoreException {
        WaitingProgressMonitor progressMonitor = new WaitingProgressMonitor();
        javaProject.getProject().delete(true, progressMonitor);
        progressMonitor.waitTillDone(5000);
    }

    public static IPackageFragmentRoot getSourceRoot(IJavaProject javaProject) throws JavaModelException {
        IPackageFragmentRoot sourceRoot = null;
        for (IPackageFragmentRoot pfr : javaProject.getPackageFragmentRoots()) {
            if (pfr.getKind() == IPackageFragmentRoot.K_SOURCE) {
                sourceRoot = pfr;
            }
        }
        return sourceRoot;
    }

    public static IPackageFragment createPackage(IJavaProject javaProject, String packageName) throws JavaModelException {
        IPackageFragmentRoot sourceRoot = getSourceRoot(javaProject);
        IPackageFragment packageFragment = null;
        if (sourceRoot != null) {
            packageFragment = sourceRoot.getPackageFragment(packageName);
            if (!packageFragment.exists()) {
                packageFragment = sourceRoot.createPackageFragment(packageName, false, null);
            }
        }
        return packageFragment;
    }

    public static IFile createJavaFile(IJavaProject javaProject, String packageName, String className, String classSource) throws CoreException {
        IPackageFragment packageFragment = createPackage(javaProject, packageName);
        IPath sourcePath = packageFragment.getPath();
        IPath javaFilePath = new Path(sourcePath.toString());
        javaFilePath = javaFilePath.append(className);
        javaFilePath = javaFilePath.addFileExtension(JAVA_EXTENSION);
        IFile javaFile = ResourcesPlugin.getWorkspace().getRoot().getFile(javaFilePath);
        javaFile.create(new StringBufferInputStream(classSource), false, null);

        return javaFile;
    }

    public static ICompilationUnit createCompilationUnit(IFile javaFile) throws JavaModelException {
        return ((ICompilationUnit) JavaCore.create(javaFile)).getWorkingCopy(null);
    }

    public static int runJavaFile(IJavaProject javaProject, String mainClassQName, String[] args, final Writer out, final Writer err) throws CoreException, InterruptedException {
      ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
      ILaunchConfigurationType type = manager.getLaunchConfigurationType(IJavaLaunchConfigurationConstants.ID_JAVA_APPLICATION);
      ILaunchConfigurationWorkingCopy wc = type.newInstance(null, "TestConfig-" + javaProject.getElementName());
      wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, javaProject.getElementName());
      wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, mainClassQName);
      StringBuffer argsLine = new StringBuffer();
      if (args != null) {
          for (String arg : args) {
              if (argsLine.length() > 0) {
                  argsLine.append(" ");
              }
              argsLine.append(arg);
          }
      }
      wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS, argsLine.toString());
      wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, "-ea");

      ILaunchConfiguration config = wc.doSave();
      WaitingProgressMonitor progressMonitor = new WaitingProgressMonitor();
      ILaunch launch = config.launch(ILaunchManager.RUN_MODE, progressMonitor, true, false);
      progressMonitor.waitTillDone(5000);
      int exitValue = -1;
      if (launch.getProcesses().length == 1) {
          IProcess iProcess = launch.getProcesses()[0];
          if (out != null) {
              iProcess.getStreamsProxy().getOutputStreamMonitor().addListener(new IStreamListener() {
                  public void streamAppended(String text, IStreamMonitor monitor) {
                      try {
                          out.write(text);
                          out.flush();
                      } catch (IOException e) { }
                  }
              });
          }
          if (err != null) {
              iProcess.getStreamsProxy().getErrorStreamMonitor().addListener(new IStreamListener() {
                  public void streamAppended(String text, IStreamMonitor monitor) {
                      try {
                          err.write(text);
                          err.flush();
                      } catch (IOException e) { }
                  }
              });
          }

          while(!iProcess.isTerminated()) {
              Thread.sleep(100);
          }
          exitValue = iProcess.getExitValue();
      }

      return exitValue;
    }

    public static int runTestCase(IJavaProject javaProject, String testClassQName) throws CoreException, InterruptedException {
        return runJavaFile(javaProject, "junit.textui.TestRunner", new String[]{testClassQName}, null, null);
    }

    @SuppressWarnings("unchecked")
    private static <T> T[] merge(T[]... arrays) {
        ArrayList<T> list = new ArrayList<T>();
        for (T[] a : arrays) {
            list.addAll(Arrays.asList(a));
        }
        T[] mergedArray = (T[])Array.newInstance(arrays[0][0].getClass(), list.size());
        return list.toArray(mergedArray);
    }

    static class WaitingProgressMonitor implements IProgressMonitor {
        boolean isDone = false;

        public void beginTask(String name, int totalWork) {
        }

        public void done() {
            isDone = true;
        }

        public void internalWorked(double work) {
        }

        public boolean isCanceled() {
            return false;
        }

        public void setCanceled(boolean value) {
            isDone = true;
        }

        public void setTaskName(String name) {
        }

        public void subTask(String name) {
        }

        public void worked(int work) {
        }

        public void waitTillDone(long timeout) {
            try {
                while (!isDone && timeout > 0) {
                    Thread.sleep(100);
                    timeout -= 100;
                }
            } catch (InterruptedException e) {
            }
        }
    }
}
