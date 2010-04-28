/*
* Copyright (c) 2009 by Sabre Holdings Corp.
* 3150 Sabre Drive, Southlake, TX 76092 USA
* All rights reserved.
*
* This software is the confidential and proprietary information
* of Sabre Holdings Corporation ("Confidential Information").
* You shall not disclose such Confidential Information and shall
* use it only in accordance with the terms of the license agreement
* you entered into with Sabre Holdings Corporation.
*/

package com.sabre.buildergenerator.ui.wizard;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.sabre.buildergenerator.Activator;

/**
 * Title: ErrorCreator.java<br>
 * Description: <br>
 * Created: Apr 28, 2010<br>
 * Copyright: Copyright (c) 2007<br>
 * Company: Sabre Holdings Corporation
 * @author Jakub Janczak sg0209399
 * @version $Rev$: , $Date$: , $Author$:
 */

public class ErrorCreator {

	IStatus createError(String message) {
		return new Status(Status.ERROR, Activator.PLUGIN_ID, message);
	}

	Status createWarning(String message) {
		return new Status(Status.WARNING, Activator.PLUGIN_ID,
				"Discouraged type name: " + message);
	}

}
