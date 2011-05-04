package com.sabre.buildergenerator.signatureutils;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import static com.sabre.buildergenerator.signatureutils.SignatureParser.*;

public class TypenameParserTest {
    @Mock
    private SignatureHandler listener;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void shouldParseSimpleType() throws SignatureParserException, ExceptionWrapper {
        // given
        String input = "int";
        TypenameParser parser = new TypenameParser(input, listener);

        // when
        parser.parse();

        // then
        Mockito.verify(listener).simpleType(SignatureParser.C_INT);
    }

    @Test
    public void shouldParseClassType() throws SignatureParserException, ExceptionWrapper {
        // given
        String input = "mypackage.MyClass";
        TypenameParser parser = new TypenameParser(input, listener);

        // when
        parser.parse();

        // then
        InOrder inOrder = Mockito.inOrder(listener);
        inOrder.verify(listener).startResolvedType("mypackage.MyClass");
        inOrder.verify(listener).endType();
    }

    @Test
    public void shouldParseArrayOfSimpleType() throws SignatureParserException, ExceptionWrapper {
        // given
        String input = "int[]";
        TypenameParser parser = new TypenameParser(input, listener);

        // when
        parser.parse();

        // then
        InOrder inOrder = Mockito.inOrder(listener);
        inOrder.verify(listener).simpleType(C_INT);
        inOrder.verify(listener).array();
    }

    @Test
    public void shouldParseArrayOfClassType() throws SignatureParserException, ExceptionWrapper {
        // given
        String input = "mypackage.MyClass[]";
        TypenameParser parser = new TypenameParser(input, listener);

        // when
        parser.parse();

        // then
        InOrder inOrder = Mockito.inOrder(listener);
        inOrder.verify(listener).startResolvedType("mypackage.MyClass");
        inOrder.verify(listener).array();
        inOrder.verify(listener).endType();
    }

    @Test
    public void shouldParseParametrizedType() throws SignatureParserException, ExceptionWrapper {
        // given
        String input = "java.util.Collection<String>";
        TypenameParser parser = new TypenameParser(input, listener);

        // when
        parser.parse();

        // then
        InOrder inOrder = Mockito.inOrder(listener);
        inOrder.verify(listener).startResolvedType("java.util.Collection");
        inOrder.verify(listener).startTypeArguments();
        inOrder.verify(listener).startResolvedType("String");
        inOrder.verify(listener).endType();
        inOrder.verify(listener).endTypeArguments();
        inOrder.verify(listener).endType();
    }

    @Test
    public void shouldParseParametrizedTypeWithManyParams() throws SignatureParserException, ExceptionWrapper {
        // given
        String input = "java.util.Collection<String,int[],Character>";
        TypenameParser parser = new TypenameParser(input, listener);

        // when
        parser.parse();

        // then
        InOrder inOrder = Mockito.inOrder(listener);
        inOrder.verify(listener).startResolvedType("java.util.Collection");
        inOrder.verify(listener).startTypeArguments();
        inOrder.verify(listener).startResolvedType("String");
        inOrder.verify(listener).endType();
        inOrder.verify(listener).simpleType(C_INT);
        inOrder.verify(listener).array();
        inOrder.verify(listener).startResolvedType("Character");
        inOrder.verify(listener).endType();
        inOrder.verify(listener).endTypeArguments();
        inOrder.verify(listener).endType();
    }

    @Test
    public void shouldParseParametrizedTypeWithUpperBound() throws SignatureParserException, ExceptionWrapper {
        // given
        String input = "java.util.Collection<? extends String>";
        TypenameParser parser = new TypenameParser(input, listener);

        // when
        parser.parse();

        // then
        InOrder inOrder = Mockito.inOrder(listener);
        inOrder.verify(listener).startResolvedType("java.util.Collection");
        inOrder.verify(listener).startTypeArguments();
        inOrder.verify(listener).wildcardAny();
        inOrder.verify(listener).wildcardExtends();
        inOrder.verify(listener).startResolvedType("String");
        inOrder.verify(listener).endType();
        inOrder.verify(listener).endTypeArguments();
        inOrder.verify(listener).endType();
    }
}
