package com.pw.compiler;

import com.google.auto.service.AutoService;
import com.pw.annotation.Lance;
import com.pw.annotation.inject.InjectView;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;


/**
 * 注解处理器
 * 用来生成代码的
 * 使用前需要注册
 */
//@SupportedAnnotationTypes("com.pw.annotation.Lance")
//@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class LanceProcessor extends AbstractProcessor {

    //1.支持的版本
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    //2.能用来处理哪些注解
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new HashSet<>();
        types.add(Lance.class.getCanonicalName());
        types.add(InjectView.class.getCanonicalName());
//        return super.getSupportedAnnotationTypes();
        return types;
    }

    Filer mFiler;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mFiler = processingEnv.getFiler();
    }


    /**
     * 1、process方法是怎么被回调的？ javac : proc.process(tes,renv)
     * 2、调用多少次是怎么确定的？ 有生成新文件 ：2*n+1 ；没有生成新文件 ： n+1
     * 3、返回值有什么用?  是否往下继续传递 ，返回true则不处理后续的注解
     * 4、自定义APT类如果有多个，如何控制他们的执行顺序  配置文件决定的
     */

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Messager messager = processingEnv.getMessager();
        messager.printMessage(Diagnostic.Kind.NOTE,"test======================="+annotations);

        if (annotations == null || annotations.isEmpty())  {
            return false;
        }

        Set<? extends Element> elementsAnnotatedWith = roundEnv.getElementsAnnotatedWith(InjectView.class);

        //根据activity 将 element 分组
        Map<String, List<VariableElement>> map = new HashMap<>();
        for (Element element : elementsAnnotatedWith) {
            VariableElement variableElement = (VariableElement) element;
            String activityName =
                    variableElement
                            .getEnclosingElement() //获取注解包裹的元素
                            .getSimpleName()
                            .toString();
            List<VariableElement> variableElements = map.get(activityName);
            if (variableElements == null) {
                variableElements = new ArrayList<>();
                map.put(activityName, variableElements);
            }
            variableElements.add(variableElement);
        }

        if (map.size() > 0) {
            Writer writer = null;
            Iterator<String> iterator = map.keySet().iterator();
            while (iterator.hasNext()) {
                String activityName = iterator.next();
                List<VariableElement> variableElements = map.get(activityName);
                if (variableElements.size()>0) {
                    VariableElement variableElement = variableElements.get(0);
                    if (variableElement != null) {
                        TypeElement enclosingElement = (TypeElement) variableElement.getEnclosingElement();
                        String packageName = processingEnv.getElementUtils().getPackageOf(enclosingElement).toString();
                        try {
                            JavaFileObject sourceFile = mFiler.createSourceFile(packageName+"."+activityName+"_ViewBinding");
                            writer = sourceFile.openWriter();
                            writer.write("package "+packageName+";\n");
                            writer.write("import com.pw.annotation.inject.PWBinder;\n");
                            writer.write("import android.graphics.Color;\n");
                            writer.write("import android.view.ViewGroup;\n");
                            writer.write("public class "+activityName+"_ViewBinding implements PWBinder<"+packageName+"."+activityName+">{\n");
                            writer.write("@Override\n"+" public void bind("+packageName+"."+activityName+" target){");
                            for (VariableElement element : variableElements) {
                                if (element != null) {
                                    String variableName = element.getSimpleName().toString();
                                    int value = variableElement.getAnnotation(InjectView.class).value();
                                    TypeMirror typeMirror = variableElement.asType();
                                    writer.write("target."+variableName+"=("+typeMirror+")target.findViewById("+value+");\n");
                                    writer.write("((ViewGroup)target."+variableName+".getParent()).setBackgroundColor(Color.RED);\n");
                                }
                            }
                            writer.write("\n}}");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }finally {
                            if (writer != null) {
                                try {
                                    writer.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
        }


        return false;
    }
}
