package org.jetbrains.jps.incremental;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.Module;
import org.jetbrains.jps.Project;
import org.jetbrains.jps.artifacts.Artifact;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Eugene Zhuravlev
 *         Date: 9/17/11
 */
public class ModulesScope extends CompileScope {

  private final Set<String> myModules;
  private final boolean myForcedCompilation;

  public ModulesScope(Project project, Set<Module> modules, Set<Artifact> artifacts, boolean isForcedCompilation) {
    super(project, artifacts);
    myModules = new HashSet<String>();
    for (Module module : modules) {
      myModules.add(module.getName());
    }
    myForcedCompilation = isForcedCompilation;
  }

  public boolean isRecompilationForced(@NotNull String moduleName) {
    return myForcedCompilation && isAffected(moduleName);
  }

  public boolean isAffected(@NotNull String moduleName) {
    return myModules.contains(moduleName);
  }

  public boolean isAffected(String moduleName, @NotNull File file) {
    return true; // for speed reasons
  }

}
