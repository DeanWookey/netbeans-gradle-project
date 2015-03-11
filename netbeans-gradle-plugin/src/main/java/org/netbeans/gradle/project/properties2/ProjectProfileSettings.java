package org.netbeans.gradle.project.properties2;

import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jtrim.cancel.Cancellation;
import org.jtrim.cancel.CancellationToken;
import org.jtrim.concurrent.CancelableTask;
import org.jtrim.concurrent.TaskExecutorService;
import org.jtrim.concurrent.TaskFuture;
import org.jtrim.concurrent.WaitableSignal;
import org.jtrim.property.MutableProperty;
import org.jtrim.utils.ExceptionHelper;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.gradle.project.NbGradleProject;
import org.netbeans.gradle.project.NbTaskExecutors;
import org.netbeans.gradle.project.properties.DomElementKey;
import org.netbeans.gradle.project.properties.SettingsFiles;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.w3c.dom.Element;

public final class ProjectProfileSettings {
    private static final Logger LOGGER = Logger.getLogger(ProjectProfileSettings.class.getName());
    // Should be single threaded to avoid unnecessary multiple load.
    private static final TaskExecutorService SAVE_LOAD_EXECUTOR
            = NbTaskExecutors.newExecutor("Profile-I/O", 1);

    private final ProfileSettingsKey key;
    private final ProfileSettings settings;

    private final WaitableSignal loadedOnceSignal;

    public ProjectProfileSettings(ProfileSettingsKey key) {
        ExceptionHelper.checkNotNullArgument(key, "key");

        this.key = key;
        this.settings = new ProfileSettings();
        this.loadedOnceSignal = new WaitableSignal();
    }

    public ProfileSettingsKey getKey() {
        return key;
    }

    public static boolean isEventThread() {
        return ProfileSettings.isEventThread();
    }

    private Path tryGetProfileFile() throws IOException {
        Project project = tryGetProject();
        return project != null ? tryGetProfileFile(project) : null;
    }

    private Project tryGetProject() throws IOException {
        Path projectDir = key.getProjectDir();
        FileObject projectDirObj = FileUtil.toFileObject(projectDir.toFile());

        return ProjectManager.getDefault().findProject(projectDirObj);
    }

    private Path tryGetProfileFile(Project project) {
        NbGradleProject gradleProject = project.getLookup().lookup(NbGradleProject.class);
        if (gradleProject == null) {
            LOGGER.log(Level.WARNING, "Not a Gradle project: {0}", project.getProjectDirectory());
            return null;
        }

        return SettingsFiles.getProfileFile(gradleProject, key.getKey());
    }

    public void ensureLoaded() {
        if (loadedOnceSignal.isSignaled()) {
            return;
        }

        SAVE_LOAD_EXECUTOR.execute(Cancellation.UNCANCELABLE_TOKEN, new CancelableTask() {
            @Override
            public void execute(CancellationToken cancelToken) throws IOException {
                if (!loadedOnceSignal.isSignaled()) {
                    loadNow();
                }
            }
        }, null);
    }

    public void ensureLoadedAndWait() {
        ensureLoaded();
        loadedOnceSignal.waitSignal(Cancellation.UNCANCELABLE_TOKEN);
    }

    public void loadEventually() {
        SAVE_LOAD_EXECUTOR.execute(Cancellation.UNCANCELABLE_TOKEN, new CancelableTask() {
            @Override
            public void execute(CancellationToken cancelToken) throws IOException {
                loadNow();
            }
        }, null);
    }

    public void loadAndWait() {
        TaskFuture<?> loadFuture = SAVE_LOAD_EXECUTOR.submit(Cancellation.UNCANCELABLE_TOKEN, new CancelableTask() {
            @Override
            public void execute(CancellationToken cancelToken) throws IOException {
                loadNow();
            }
        }, null);

        loadFuture.waitAndGet(Cancellation.UNCANCELABLE_TOKEN);
    }

    private void loadNow() throws IOException {
        try {
            Path profileFile = tryGetProfileFile();
            if (profileFile == null) {
                LOGGER.log(Level.WARNING, "Cannot find location to save the profile: {0}", key);
                return;
            }

            settings.loadFromFile(profileFile);
        } finally {
            loadedOnceSignal.signal();
        }
    }

    public void saveEventually() {
        SAVE_LOAD_EXECUTOR.execute(Cancellation.UNCANCELABLE_TOKEN, new CancelableTask() {
            @Override
            public void execute(CancellationToken cancelToken) throws IOException {
                saveNow();
            }
        }, null);
    }

    public void saveAndWait() {
        TaskFuture<?> saveFuture = SAVE_LOAD_EXECUTOR.submit(Cancellation.UNCANCELABLE_TOKEN, new CancelableTask() {
            @Override
            public void execute(CancellationToken cancelToken) throws IOException {
                saveNow();
            }
        }, null);

        saveFuture.waitAndGet(Cancellation.UNCANCELABLE_TOKEN);
    }

    private void saveNow() throws IOException {
        Project project = tryGetProject();
        if (project == null) {
            LOGGER.log(Level.WARNING, "No project in {0}", key.getProjectDir());
            return;
        }

        Path profileFile = tryGetProfileFile(project);
        if (profileFile == null) {
            return;
        }

        settings.saveToFile(project, profileFile);
    }

    public Element getAuxConfigValue(DomElementKey key) {
        return settings.getAuxConfigValue(key);
    }

    public <ValueKey, ValueType> MutableProperty<ValueType> getProperty(PropertyDef<ValueKey, ValueType> propertyDef) {
        return settings.getProperty(propertyDef);
    }
}