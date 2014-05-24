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

import java.util.ArrayList;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.perfcake.ScenarioExecution;

/**
 * 
 * @author vjuranek
 * 
 */
@Mojo(name = "scenario-run", defaultPhase = LifecyclePhase.INTEGRATION_TEST)
public class ScenarioRunMojo extends AbstractMojo {

   @Parameter(required = true)
   private String scenario;
   @Parameter(alias = "scenarios-dir")
   private String scenariosDir;
   @Parameter(alias = "messages-dir")
   private String messagesDir;
   @Parameter(alias = "plugins-dir")
   private String pluginsDir;
   
   public void execute() throws MojoExecutionException {
      ArrayList<String> args = new ArrayList<String>();
      args.add("-s");
      args.add(scenario);
      if(scenariosDir != null && !scenariosDir.trim().equals("")) {
         args.add("-sd");
         args.add(scenariosDir);
      }
      if(messagesDir != null && !messagesDir.trim().equals("")) {
         args.add("-md");
         args.add(messagesDir);
      }
      if(pluginsDir != null && !pluginsDir.trim().equals("")) {
         args.add("-pd");
         args.add(pluginsDir);
      }
      
      System.out.println("Running scenario " + scenario);
      ScenarioExecution.main(args.toArray(new String[args.size()]));
      System.out.println("DONE scenario " + scenario);
   }
}
