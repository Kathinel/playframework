/*
 * Copyright (C) from 2022 The Play Framework Contributors <https://github.com/playframework>, 2011-2021 Lightbend Inc. <https://www.lightbend.com>
 */
package play.gradle.task;

import javax.inject.Inject;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.FileType;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.SetProperty;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.Classpath;
import org.gradle.api.tasks.IgnoreEmptyDirectories;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.api.tasks.TaskAction;
import org.gradle.work.FileChange;
import org.gradle.work.Incremental;
import org.gradle.work.InputChanges;
import org.gradle.workers.WorkQueue;
import org.gradle.workers.WorkerExecutor;
import play.gradle.internal.RoutesCompileAction;

/** Gradle task for compiling Routes files. */
@CacheableTask
public abstract class RoutesCompile extends DefaultTask {

  @InputFiles
  @Incremental
  @IgnoreEmptyDirectories
  @PathSensitive(PathSensitivity.RELATIVE)
  public abstract ConfigurableFileCollection getSource();

  @Classpath
  public abstract ConfigurableFileCollection getRoutesCompilerClasspath();

  @Input
  public abstract Property<Boolean> getNamespaceReverseRouter();

  @Input
  public abstract Property<Boolean> getGenerateReverseRouter();

  @Input
  public abstract SetProperty<String> getImports();

  @OutputDirectory
  public abstract DirectoryProperty getDestinationDirectory();

  @Inject
  public abstract WorkerExecutor getWorkerExecutor();

  @TaskAction
  void compile(InputChanges changes) {
    for (FileChange change : changes.getFileChanges(getSource())) {
      if (change.getFileType() == FileType.DIRECTORY) continue;
      WorkQueue workQueue =
          getWorkerExecutor()
              .classLoaderIsolation(spec -> spec.getClasspath().from(getRoutesCompilerClasspath()));

      workQueue.submit(
          RoutesCompileAction.class,
          parameters -> {
            parameters.getChangeType().set(change.getChangeType());
            parameters.getRoutesFile().set(change.getFile());
            parameters.getDestinationDirectory().set(getDestinationDirectory());
            parameters.getGenerateReverseRouter().set(getGenerateReverseRouter());
            parameters.getNamespaceReverseRouter().set(getNamespaceReverseRouter());
            parameters.getImports().set(getImports());
          });
    }
  }
}
