package com.emir.gitautocommit.service;

import com.emir.gitautocommit.exception.GitOperationException;
import com.emir.gitautocommit.util.StrUtils;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


@Slf4j
@Service
public class JGitCommitService {

    private UsernamePasswordCredentialsProvider credentialsProvider = null;

    public boolean loginWithToken(String username, String token) {
        if (username == null || username.isBlank() || token == null || token.isBlank()) {
            throw new GitOperationException("Username and token are required for Git authentication");
        }
        try {
            this.credentialsProvider = new UsernamePasswordCredentialsProvider(username, token);
            log.info("Git authentication successful for user: {}", username);
            return true;
        } catch (Exception e) {
            throw new GitOperationException("Invalid credentials format");
        }
    }

    public void logout() {
        credentialsProvider = null;
        log.info("Git credentials cleared successfully");
    }

    public void testConnection(String repositoryUrl) {
        if (credentialsProvider == null) {
            throw new GitOperationException("No credentials available. Please login first.");
        }

        if (repositoryUrl == null || repositoryUrl.isBlank()) {
            throw new GitOperationException("Repository URL is required for connection test");
        }

        try {
            log.debug("Testing connection to Git repository: {}", repositoryUrl);
            Git.lsRemoteRepository()
                .setRemote(repositoryUrl)
                .setHeads(true)
                .setTags(true)
                .setCredentialsProvider(credentialsProvider)
                .setTimeout(30000) // 30 seconds timeout
                .call();
            
            log.info("Git repository connection test successful: {}", repositoryUrl);
        } catch (GitAPIException e) {
            throw new GitOperationException("Failed to connect to repository: " + e.getMessage(), e);
        }
    }

    public void makeNCommits(Integer commitCount, String repoPath, String branch, String remoteUrl, String username, String email) {
        log.info("Starting commit operation - Count: {}, Branch: {}, Repository: {}", commitCount, branch, remoteUrl);
        validateMakeCommitsParameters(commitCount, repoPath, branch, remoteUrl);
        
        if (credentialsProvider == null) {
            throw new GitOperationException("Git authentication required. Please login before performing Git operations.");
        }

        File repoDir = new File(repoPath);
        Git git = null;

        try {
            git = setupRepository(repoDir, remoteUrl);
            checkoutBranch(git, branch);
            pullChanges(git);

            File commitFile = createOrGetCommitFile(repoDir);
            performCommits(git, commitFile, commitCount, username, email);

            pullChanges(git);
            pushChanges(git);
            log.info("Commit operation completed successfully - {} commit(s) pushed to branch '{}'", commitCount, branch);
        } catch (IOException e) {
            throw new GitOperationException("Failed to perform file operations: " + e.getMessage(), e);
        } catch (GitAPIException e) {
            throw new GitOperationException("Failed to perform Git operations: " + e.getMessage(), e);
        } finally {
            if (git != null) {
                git.close();
                log.debug("Git repository connection closed");
            }
        }
    }

    private Git setupRepository(File repoDir, String remoteUrl) throws IOException, GitAPIException {
        if (!repoDir.exists()) {
            log.debug("Repository directory does not exist, creating and cloning from {}", remoteUrl);
            repoDir.mkdirs();
            return cloneRepository(repoDir, remoteUrl);
        } else {
            File gitDir = new File(repoDir, ".git");
            if (!gitDir.exists()) {
                log.debug("Directory exists but is not a Git repository, initializing from {}", remoteUrl);
                clearDirectory(repoDir);
                return cloneRepository(repoDir, remoteUrl);
            } else {
                log.debug("Opening existing Git repository at {}", repoDir.getAbsolutePath());
                return openRepository(repoDir);
            }
        }
    }

    private Git cloneRepository(File repoDir, String remoteUrl) throws GitAPIException {
        log.info("Cloning Git repository - Source: {}, Target: {}", remoteUrl, repoDir.getAbsolutePath());
        try {
            Git git = Git.cloneRepository()
                    .setURI(remoteUrl)
                    .setDirectory(repoDir)
                    .setCredentialsProvider(credentialsProvider)
                    .call();
            log.info("Repository cloned successfully");
            return git;
        } catch (GitAPIException e) {
            log.error("Repository cloning failed: {}", e.getMessage());
            throw new GitOperationException("Failed to clone repository: " + e.getMessage(), e);
        }
    }

    private Git openRepository(File repoDir) throws IOException {
        try {
            FileRepositoryBuilder builder = new FileRepositoryBuilder();
            Repository repository = builder.setGitDir(new File(repoDir, ".git"))
                    .readEnvironment()
                    .findGitDir()
                    .build();
            return new Git(repository);
        } catch (IOException e) {
            log.error("Failed to open existing repository: {}", e.getMessage());
            throw new GitOperationException("Failed to open repository: " + e.getMessage(), e);
        }
    }

    private void checkoutBranch(Git git, String branch) throws GitAPIException {
        try {
            log.debug("Checking out branch: {}", branch);
            git.checkout().setName(branch).call();
            log.debug("Branch checkout successful: {}", branch);
        } catch (GitAPIException e) {
            log.error("Branch checkout failed for '{}': {}", branch, e.getMessage());
            throw new GitOperationException("Failed to checkout branch: " + e.getMessage(), e);
        }
    }

    private void pullChanges(Git git) throws GitAPIException {
        try {
            log.debug("Pulling latest changes from remote");
            git.pull()
                    .setCredentialsProvider(credentialsProvider)
                    .call();
            log.debug("Pull operation completed successfully");
        } catch (GitAPIException e) {
            log.error("Pull operation failed: {}", e.getMessage());
            throw new GitOperationException("Failed to pull changes: " + e.getMessage(), e);
        }
    }

    private void pushChanges(Git git) throws GitAPIException {
        try {
            log.debug("Pushing changes to remote repository");
            git.push()
                    .setCredentialsProvider(credentialsProvider)
                    .call();
            log.debug("Push operation completed successfully");
        } catch (GitAPIException e) {
            log.error("Push operation failed: {}", e.getMessage());
            throw new GitOperationException("Failed to push changes: " + e.getMessage(), e);
        }
    }

    private File createOrGetCommitFile(File repoDir) throws IOException {
        File commitFile = new File(repoDir, "daily_commit.txt");
        if (!commitFile.exists()) {
            log.debug("Creating commit file: {}", commitFile.getAbsolutePath());
            if (!commitFile.createNewFile()) {
                log.error("Failed to create commit file: {}", commitFile.getAbsolutePath());
                throw new GitOperationException("Failed to create commit file");
            }
        }
        return commitFile;
    }

    private void performCommits(Git git, File commitFile, int commitCount, String username, String email) throws IOException, GitAPIException {
        log.debug("Starting commit sequence - Total commits to create: {}", commitCount);
        for (int i = 0; i < commitCount; i++) {
            appendRandomLine(commitFile);
            git.add().addFilepattern("daily_commit.txt").call();
            git.commit()
                    .setMessage(StrUtils.getRandomCommitMessage())
                    .setAuthor(username, email)
                    .setCommitter(username, email)
                    .call();
            log.debug("Created commit {}/{}", i + 1, commitCount);
        }
        log.debug("Commit sequence completed successfully");
    }

    private void appendRandomLine(File file) throws IOException {
        try (FileWriter writer = new FileWriter(file, true)) {
            writer.write(StrUtils.generateRandomString(10) + "\n");
        } catch (IOException e) {
            throw new GitOperationException("Failed to append line to commit file: " + e.getMessage(), e);
        }
    }

    public void clearDirectory(File directory) throws IOException {
        if (directory.exists() && directory.isDirectory()) {
            log.debug("Clearing directory: {}", directory.getAbsolutePath());
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        clearDirectory(file);
                    }
                    if (!file.delete()) {
                        log.error("Failed to delete file: {}", file.getAbsolutePath());
                        throw new GitOperationException("Failed to delete " + file.getAbsolutePath());
                    }
                }
            }
            log.debug("Directory cleared successfully: {}", directory.getAbsolutePath());
        }
    }

    private void validateMakeCommitsParameters(Integer commitCount, String repoPath, String branch, String remoteUrl) {
        if (repoPath == null || repoPath.isBlank()) {
            log.error("Invalid parameter: Repository path is required");
            throw new IllegalArgumentException("Repository path is required");
        }
        if (commitCount == null || commitCount <= 0) {
            log.error("Invalid parameter: Commit count must be positive, received: {}", commitCount);
            throw new IllegalArgumentException("Commit count must be a positive integer");
        }
        if (branch == null || branch.isBlank()) {
            log.error("Invalid parameter: Branch name is required");
            throw new IllegalArgumentException("Branch name is required");
        }
        if (remoteUrl == null || remoteUrl.isBlank()) {
            log.error("Invalid parameter: Remote repository URL is required");
            throw new IllegalArgumentException("Remote repository URL is required");
        }
        log.debug("Parameter validation successful - CommitCount: {}, Branch: {}, Repository: {}", 
                 commitCount, branch, remoteUrl);
    }
}
