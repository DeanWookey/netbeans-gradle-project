package org.netbeans.gradle.model.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.gradle.api.Project;
import org.netbeans.gradle.model.api.GradleProjectInfoQuery2;
import org.netbeans.gradle.model.api.ModelClassPathDef;
import org.netbeans.gradle.model.api.ProjectInfoBuilder2;
import org.netbeans.gradle.model.internal.BuilderWrapper;

@SuppressWarnings("deprecation")
public final class CompatibilityUtils {
    public static Collection<GradleProjectInfoQuery2<?>> toQuery2All(
            Collection<? extends org.netbeans.gradle.model.api.GradleProjectInfoQuery<?>> src) {
        List<GradleProjectInfoQuery2<?>> result = new ArrayList<GradleProjectInfoQuery2<?>>(src.size());
        for (org.netbeans.gradle.model.api.GradleProjectInfoQuery<?> query: src) {
            result.add(toQuery2(query));
        }
        return result;
    }

    public static Collection<org.netbeans.gradle.model.api.GradleProjectInfoQuery<?>> toQueryAll(
            Collection<? extends GradleProjectInfoQuery2<?>> src) {
        List<org.netbeans.gradle.model.api.GradleProjectInfoQuery<?>> result
                = new ArrayList<org.netbeans.gradle.model.api.GradleProjectInfoQuery<?>>(src.size());
        for (GradleProjectInfoQuery2<?> query: src) {
            result.add(toQuery(query));
        }
        return result;
    }

    public static <T> GradleProjectInfoQuery2<T> toQuery2(org.netbeans.gradle.model.api.GradleProjectInfoQuery<T> src) {
        return new GradleProjectInfoQuery2Wrapper<T>(src);
    }

    public static <T> ProjectInfoBuilder2<T> toBuilder2(org.netbeans.gradle.model.api.ProjectInfoBuilder<T> src) {
        return new ProjectInfoBuilder2Wrapper<T>(src);
    }

    public static <T> org.netbeans.gradle.model.api.GradleProjectInfoQuery<T> toQuery(GradleProjectInfoQuery2<T> src) {
        return new GradleProjectInfoQueryWrapper<T>(src);
    }

    public static <T> org.netbeans.gradle.model.api.ProjectInfoBuilder<T> toBuilder(ProjectInfoBuilder2<T> src) {
        return new ProjectInfoBuilderWrapper<T>(src);
    }

    private static final class ProjectInfoBuilder2Wrapper<T>
    implements
            ProjectInfoBuilder2<T>,
            BuilderWrapper {
        private static final long serialVersionUID = 1L;

        private final org.netbeans.gradle.model.api.ProjectInfoBuilder<T> src;

        public ProjectInfoBuilder2Wrapper(org.netbeans.gradle.model.api.ProjectInfoBuilder<T> src) {
            if (src == null) throw new NullPointerException("src");
            this.src = src;
        }

        @Override
        public T getProjectInfo(Object project) {
            return src.getProjectInfo((Project)project);
        }

        @Override
        public String getName() {
            return src.getName();
        }

        @Override
        public Object getWrappedObject() {
            return src;
        }

        @Override
        public Class<?> getWrappedType() {
            return getWrappedObject().getClass();
        }
    }

    @SuppressWarnings("deprecation")
    private static class GradleProjectInfoQuery2Wrapper<T>
    implements
            GradleProjectInfoQuery2<T>,
            BuilderWrapper {
        private final org.netbeans.gradle.model.api.GradleProjectInfoQuery<T> src;

        public GradleProjectInfoQuery2Wrapper(org.netbeans.gradle.model.api.GradleProjectInfoQuery<T> src) {
            if (src == null) throw new NullPointerException("src");
            this.src = src;
        }

        @Override
        public ProjectInfoBuilder2<T> getInfoBuilder() {
            return toBuilder2(src.getInfoBuilder());
        }

        @Override
        public ModelClassPathDef getInfoClassPath() {
            return src.getInfoClassPath();
        }

        @Override
        public Object getWrappedObject() {
            return src;
        }

        @Override
        public Class<?> getWrappedType() {
            return getWrappedObject().getClass();
        }
    }

    /** @deprecated  */
    @Deprecated
    private static final class ProjectInfoBuilderWrapper<T>
    implements
            org.netbeans.gradle.model.api.ProjectInfoBuilder<T>,
            BuilderWrapper {
        private static final long serialVersionUID = 1L;

        private final ProjectInfoBuilder2<T> src;

        public ProjectInfoBuilderWrapper(ProjectInfoBuilder2<T> src) {
            if (src == null) throw new NullPointerException("src");
            this.src = src;
        }

        @Override
        public T getProjectInfo(Project project) {
            return src.getProjectInfo(project);
        }

        @Override
        public String getName() {
            return src.getName();
        }

        @Override
        public Object getWrappedObject() {
            return src;
        }

        @Override
        public Class<?> getWrappedType() {
            return getWrappedObject().getClass();
        }
    }

    /** @deprecated  */
    @Deprecated
    private static class GradleProjectInfoQueryWrapper<T>
    implements
            org.netbeans.gradle.model.api.GradleProjectInfoQuery<T>,
            BuilderWrapper {
        private final org.netbeans.gradle.model.api.GradleProjectInfoQuery2<T> src;

        public GradleProjectInfoQueryWrapper(org.netbeans.gradle.model.api.GradleProjectInfoQuery2<T> src) {
            if (src == null) throw new NullPointerException("src");
            this.src = src;
        }

        @Override
        public org.netbeans.gradle.model.api.ProjectInfoBuilder<T> getInfoBuilder() {
            return toBuilder(src.getInfoBuilder());
        }

        @Override
        public ModelClassPathDef getInfoClassPath() {
            return src.getInfoClassPath();
        }

        @Override
        public Object getWrappedObject() {
            return src;
        }

        @Override
        public Class<?> getWrappedType() {
            return getWrappedObject().getClass();
        }
    }

    private CompatibilityUtils() {
        throw new AssertionError();
    }
}
