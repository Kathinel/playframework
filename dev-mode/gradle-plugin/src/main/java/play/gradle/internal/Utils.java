/*
 * Copyright (C) from 2022 The Play Framework Contributors <https://github.com/playframework>, 2011-2021 Lightbend Inc. <https://www.lightbend.com>
 */
package play.gradle.internal;

import static org.gradle.api.tasks.SourceSet.MAIN_SOURCE_SET_NAME;
import static org.gradle.api.tasks.SourceSet.TEST_SOURCE_SET_NAME;

import org.gradle.api.Project;
import org.gradle.api.plugins.ExtensionAware;
import org.gradle.api.plugins.JavaApplication;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.ScalaSourceDirectorySet;
import org.gradle.api.tasks.SourceSet;
import org.gradle.util.GradleVersion;
import play.gradle.Language;
import play.gradle.PlayExtension;
import play.gradle.plugin.PlayPlugin;

public class Utils {

  /** Get Play version from Gradle Plugin MANIFEST.MF */
  public static String getDefaultPlayVersion() {
    return System.getProperty(
        "play.version", PlayPlugin.class.getPackage().getImplementationVersion());
  }

  public static boolean isGradleVersionLessThan(String gradleVersion) {
    return GradleVersion.current().compareTo(GradleVersion.version(gradleVersion)) < 0;
  }

  public static PlayExtension playExtension(Project project) {
    return extensionOf(project, PlayExtension.class);
  }

  public static JavaPluginExtension javaPluginExtension(Project project) {
    return extensionOf(project, JavaPluginExtension.class);
  }

  public static JavaApplication javaApplicationExtension(Project project) {
    return extensionOf(project, JavaApplication.class);
  }

  public static SourceSet mainSourceSet(Project project) {
    return javaPluginExtension(project).getSourceSets().getByName(MAIN_SOURCE_SET_NAME);
  }

  public static SourceSet testSourceSet(Project project) {
    return javaPluginExtension(project).getSourceSets().getByName(TEST_SOURCE_SET_NAME);
  }

  public static ScalaSourceDirectorySet scalaSourceDirectorySet(SourceSet sourceSet) {
    return extensionOf(sourceSet, ScalaSourceDirectorySet.class);
  }

  public static <T> T extensionOf(ExtensionAware extensionAware, Class<T> type) {
    return extensionAware.getExtensions().getByType(type);
  }

  public static boolean isPlayJava(Project project) {
    return playExtension(project).getLang().get() == Language.JAVA;
  }

  public static boolean isPlayScala(Project project) {
    return playExtension(project).getLang().get() == Language.SCALA;
  }

  private Utils() {}
}
