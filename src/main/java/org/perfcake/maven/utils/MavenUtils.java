package org.perfcake.maven.utils;

import java.io.File;
import java.util.List;

import org.apache.maven.plugin.logging.Log;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;

public class MavenUtils {

   public static File getArtifactJarFile(RepositorySystem repoSystem, List<RemoteRepository> remoteRepos, RepositorySystemSession repoSession, String mavenCoords, Log log) throws ArtifactResolutionException {
      Artifact artifact = new DefaultArtifact(mavenCoords);
      ArtifactRequest request = new ArtifactRequest();
      request.setArtifact(artifact);
      request.setRepositories(remoteRepos);
      log.debug("Resolving artifact " + artifact + " from " + remoteRepos);
      ArtifactResult result = repoSystem.resolveArtifact(repoSession, request);
      log.debug("Resolved artifact " + artifact + " to " + result.getArtifact().getFile() + " from " + result.getRepository());
      return result.getArtifact().getFile();
   }

}
