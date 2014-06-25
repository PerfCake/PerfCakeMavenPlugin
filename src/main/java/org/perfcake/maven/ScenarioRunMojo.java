package org.perfcake.maven;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
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

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;

/**
 * 
 * @author vjuranek
 * 
 */
@Mojo(name = "scenario-run", defaultPhase = LifecyclePhase.INTEGRATION_TEST)
public class ScenarioRunMojo extends AbstractMojo {

   private final String PERFCAKE_DIR = "perfcake";
   private final String DEFAULT_SCENARIOS_DIR = PERFCAKE_DIR + File.separator + "scenarios";
   private final String DEFAULT_MESSAGES_DIR = PERFCAKE_DIR + File.separator + "messages";
   private final String DEFAULT_PLUGINS_DIR = PERFCAKE_DIR + File.separator + "plugins";

   @Parameter(required = true)
   private String scenario;
   @Parameter(alias = "scenarios-dir")
   private String scenariosDir;
   @Parameter(alias = "messages-dir")
   private String messagesDir;
   @Parameter(alias = "plugins-dir")
   private String pluginsDir;
   @Parameter(alias = "perfcake-version")
   private String artifactCoords;

   @Parameter(defaultValue = "${project.remoteProjectRepositories}", readonly = true)
   private List<RemoteRepository> remoteRepos;
   @Parameter(defaultValue = "${repositorySystemSession}", readonly = true)
   private RepositorySystemSession repoSession;

   @Component
   private MavenProject project;
   @Component
   private RepositorySystem repoSystem;

   public void execute() throws MojoExecutionException {


      initDefaults();

      ArrayList<String> args = new ArrayList<String>();
      args.add("-s");
      args.add(scenario);

      args.add("-sd");
      args.add(scenariosDir);

      args.add("-md");
      args.add(messagesDir);

      args.add("-pd");
      args.add(pluginsDir);

      
      File perfCakeJar = getPerfCakeJarFile();
      ClassLoader currentClassLoader = this.getClass().getClassLoader();
      URL[] urls = ((URLClassLoader) currentClassLoader).getURLs();
      URL[] urlsPerfFirst = new URL[urls.length+1];
      try {
         urlsPerfFirst[0] = perfCakeJar.toURI().toURL();
      } catch(MalformedURLException e) {
         throw new MojoExecutionException(e.getMessage(), e);
      }
      System.arraycopy(urls, 0, urlsPerfFirst, 1, urls.length);
      URLClassLoader perfFirtClassLoader = new URLClassLoader(urlsPerfFirst, currentClassLoader);

      try {
         Class<?> se = perfFirtClassLoader.loadClass("org.perfcake.ScenarioExecution");
         Method main = se.getMethod("main", String[].class);
         Object[] argsArray = {args.toArray(new String[args.size()])};
         getLog().info("PerfCake: Running scenario " + scenario);
         main.invoke(null, argsArray);
         getLog().info("PerfCake: Finished scenario " + scenario);
      } catch(Exception e) {
         throw new MojoExecutionException(e.getMessage(), e);
      }
   }

   private void initDefaults() {
      String resPath = getDefaultResourcePath();

      if (scenariosDir == null || scenariosDir.trim().isEmpty()) {
         File testDir = new File(resPath + File.separator + DEFAULT_SCENARIOS_DIR);
         scenariosDir = testDir.isDirectory() ? testDir.getAbsolutePath() : resPath;
         getLog().debug("Setting PerCake scenarios dir to " + scenariosDir);
      }

      if (messagesDir == null || messagesDir.trim().isEmpty()) {
         File testDir = new File(resPath + File.separator + DEFAULT_MESSAGES_DIR);
         messagesDir = testDir.isDirectory() ? testDir.getAbsolutePath() : resPath;
         getLog().debug("Setting PerCake messages dir to " + messagesDir);
      }

      if (pluginsDir == null || pluginsDir.trim().isEmpty()) {
         File testDir = new File(resPath + File.separator + DEFAULT_PLUGINS_DIR);
         pluginsDir = testDir.isDirectory() ? testDir.getAbsolutePath() : resPath;
         getLog().debug("Setting PerCake plugins dir to " + pluginsDir);
      }
   }

   private String getDefaultResourcePath() {
      String defResPath = null;
      List<Resource> defResList = project.getBuild().getTestResources();
      if (!defResList.isEmpty() && defResList.get(0) != null) {
         defResPath = defResList.get(0).getDirectory();
      }
      return defResPath;
   }

   private File getPerfCakeJarFile() throws MojoExecutionException {
      Artifact artifact = null;
      try {
         artifact = new DefaultArtifact(artifactCoords);
      } catch (IllegalArgumentException e) {
         throw new MojoExecutionException(e.getMessage(), e);
      }

      ArtifactRequest request = new ArtifactRequest();
      request.setArtifact(artifact);
      request.setRepositories(remoteRepos);

      getLog().info("Resolving artifact " + artifact + " from " + remoteRepos);

      ArtifactResult result;
      try {
         result = repoSystem.resolveArtifact(repoSession, request);
      } catch (ArtifactResolutionException e) {
         throw new MojoExecutionException(e.getMessage(), e);
      }

      getLog().info("Resolved artifact " + artifact + " to " + result.getArtifact().getFile() + " from " + result.getRepository());
      return result.getArtifact().getFile();
   }
   
}
