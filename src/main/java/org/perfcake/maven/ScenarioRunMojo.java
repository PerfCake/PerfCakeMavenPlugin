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
import org.perfcake.ScenarioExecution;

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
   private final String DEFAULT_PLUGINS_DIR = PERFCAKE_DIR + File.separator +  "plugins";

   @Parameter(required = true)
   private String scenario;
   @Parameter(alias = "scenarios-dir")
   private String scenariosDir;
   @Parameter(alias = "messages-dir")
   private String messagesDir;
   @Parameter(alias = "plugins-dir")
   private String pluginsDir;

   @Component
   private MavenProject project;

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

      System.out.println("Running scenario " + scenario); //TODO use log
      ScenarioExecution.main(args.toArray(new String[args.size()]));
      System.out.println("DONE scenario " + scenario); //TODO use log
   }

   private void initDefaults() {
      String resPath = getDefaultResourcePath();

      if (scenariosDir == null || scenariosDir.trim().isEmpty()) {
         File testDir = new File(resPath + File.separator + DEFAULT_SCENARIOS_DIR);
         scenariosDir = testDir.isDirectory() ? testDir.getAbsolutePath() : resPath;
      }

      if (messagesDir == null || messagesDir.trim().isEmpty()) {
         File testDir = new File(resPath + File.separator + DEFAULT_MESSAGES_DIR);
         messagesDir = testDir.isDirectory() ? testDir.getAbsolutePath() : resPath;
      }

      if (pluginsDir == null || pluginsDir.trim().isEmpty()) {
         File testDir = new File(resPath + File.separator+ DEFAULT_PLUGINS_DIR);
         pluginsDir = testDir.isDirectory() ? testDir.getAbsolutePath() : resPath;
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

}
