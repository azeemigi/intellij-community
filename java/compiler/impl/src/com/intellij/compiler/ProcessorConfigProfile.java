/*
 * Copyright 2000-2012 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.intellij.compiler;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @author Eugene Zhuravlev
 *         Date: 5/25/12
 */
public class ProcessorConfigProfile implements AnnotationProcessingConfiguration {
  private String myName = "";

  private boolean myEnabled = false;
  private boolean myObtainProcessorsFromClasspath = true;
  private String myProcessorPath = "";
  private final Set<String> myProcessors = new HashSet<String>(); // empty list means all discovered
  private final Map<String, String> myProcessorOptions = new HashMap<String, String>(); // key=value map of options
  @Nullable
  private String myGeneratedSourcesDirectoryName = null; // null means 'auto'
  private final Set<String> myModuleNames = new HashSet<String>();

  public ProcessorConfigProfile(String name) {
    myName = name;
  }

  public ProcessorConfigProfile(ProcessorConfigProfile profile) {
    initFrom(profile);
  }

  public final void initFrom(ProcessorConfigProfile other) {
    myName = other.myName;
    myEnabled = other.myEnabled;
    myObtainProcessorsFromClasspath = other.myObtainProcessorsFromClasspath;
    myProcessorPath = other.myProcessorPath;
    myProcessors.clear();
    myProcessors.addAll(other.myProcessors);
    myProcessorOptions.clear();
    myProcessorOptions.putAll(other.myProcessorOptions);
    myGeneratedSourcesDirectoryName = other.myGeneratedSourcesDirectoryName;
    myModuleNames.clear();
    myModuleNames.addAll(other.myModuleNames);
  }

  public String getName() {
    return myName;
  }

  public void setName(String name) {
    myName = name;
  }

  @Override
  public boolean isEnabled() {
    return myEnabled;
  }

  public void setEnabled(boolean enabled) {
    myEnabled = enabled;
  }

  @Override
  @NotNull
  public String getProcessorPath() {
    return myProcessorPath;
  }

  public void setProcessorPath(@Nullable String processorPath) {
    myProcessorPath = processorPath != null? processorPath : "";
  }

  @Override
  public boolean isObtainProcessorsFromClasspath() {
    return myObtainProcessorsFromClasspath;
  }

  public void setObtainProcessorsFromClasspath(boolean value) {
    myObtainProcessorsFromClasspath = value;
  }

  @Override
  @Nullable
  public String getGeneratedSourcesDirectoryName() {
    return myGeneratedSourcesDirectoryName;
  }

  public void setGeneratedSourcesDirectoryName(@Nullable String generatedSourcesDirectoryName) {
    myGeneratedSourcesDirectoryName = generatedSourcesDirectoryName;
  }

  @NotNull
  public Set<String> getModuleNames() {
    return myModuleNames;
  }

  public boolean addModuleName(String name) {
    return myModuleNames.add(name);
  }

  public boolean addModuleNames(Collection<String> names) {
    return myModuleNames.addAll(names);
  }

  public boolean removeModuleName(String name) {
    return myModuleNames.remove(name);
  }

  public boolean removeModuleNames(Collection<String> names) {
    return myModuleNames.removeAll(names);
  }

  public void clearModuleNames() {
    myModuleNames.clear();
  }

  public void clearProcessors() {
    myProcessors.clear();
  }

  public boolean addProcessor(String processor) {
    return myProcessors.add(processor);
  }

  public boolean removeProcessor(String processor) {
    return myProcessors.remove(processor);
  }

  @Override
  @NotNull
  public Set<String> getProcessors() {
    return Collections.unmodifiableSet(myProcessors);
  }

  @Override
  @NotNull
  public Map<String, String> getProcessorOptions() {
    return Collections.unmodifiableMap(myProcessorOptions);
  }

  public String setOption(String key, String value) {
    return myProcessorOptions.put(key, value);
  }

  @Nullable
  public String getOption(String key) {
    return myProcessorOptions.get(key);
  }

  public void clearProcessorOptions() {
    myProcessorOptions.clear();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ProcessorConfigProfile profile = (ProcessorConfigProfile)o;

    if (myEnabled != profile.myEnabled) return false;
    if (myObtainProcessorsFromClasspath != profile.myObtainProcessorsFromClasspath) return false;
    if (myGeneratedSourcesDirectoryName != null
        ? !myGeneratedSourcesDirectoryName.equals(profile.myGeneratedSourcesDirectoryName)
        : profile.myGeneratedSourcesDirectoryName != null) {
      return false;
    }
    if (!myModuleNames.equals(profile.myModuleNames)) return false;
    if (!myProcessorOptions.equals(profile.myProcessorOptions)) return false;
    if (myProcessorPath != null ? !myProcessorPath.equals(profile.myProcessorPath) : profile.myProcessorPath != null) return false;
    if (!myProcessors.equals(profile.myProcessors)) return false;
    if (!myName.equals(profile.myName)) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = myName.hashCode();
    result = 31 * result + (myEnabled ? 1 : 0);
    result = 31 * result + (myObtainProcessorsFromClasspath ? 1 : 0);
    result = 31 * result + (myProcessorPath != null ? myProcessorPath.hashCode() : 0);
    result = 31 * result + myProcessors.hashCode();
    result = 31 * result + myProcessorOptions.hashCode();
    result = 31 * result + (myGeneratedSourcesDirectoryName != null ? myGeneratedSourcesDirectoryName.hashCode() : 0);
    result = 31 * result + myModuleNames.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return myName;
  }
}

