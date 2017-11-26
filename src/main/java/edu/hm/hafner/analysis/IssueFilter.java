package edu.hm.hafner.analysis;

import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import static com.google.common.collect.Lists.*;
import static java.util.regex.Pattern.*;
import static java.util.stream.Collectors.*;

/**
 * Filter for {@link Issues issues}.
 * <p>
 * The filtering can be performed based on a combination of the following properties of an {@link Issue issue}: <ul>
 * <li>{@link Issue#getFileName file name}</li> <li>{@link Issue#getPackageName package name}</li> <li>{@link
 * Issue#getModuleName module name}</li> <li>{@link Issue#getCategory category}</li> <li>{@link Issue#getType type}</li>
 * </ul>
 * <p>
 * A list of include and exclude regex can be defined for each property.
 * <p>
 * An {@link Issue issue} is contained in the output if at least one include filter matches and no exclude filter
 * matches. If no include filter is specified all issues are included in the filtering.
 *
 * @author Marcel Binder
 */
public class IssueFilter {
    private final List<IssuePropertyFilter> includeFilters;
    private final List<IssuePropertyFilter> excludeFilters;

    private IssueFilter(final List<IssuePropertyFilter> includeFilters, final List<IssuePropertyFilter> excludeFilters) {
        this.includeFilters = includeFilters;
        this.excludeFilters = excludeFilters;
    }

    /**
     * Filter given {@link Issues issues} by the specified include and exclude filters.
     *
     * @param issues
     *         the {@link Issues issues} to be filtered, must not be {@code null}
     *
     * @return the filtered {@link Issues issues}
     */
    public Issues filter(final Issues issues) {
        Collection<Issue> filteredIssues = issues.all()
                .stream()
                .filter(this::isIncluded)
                .filter(this::isExcluded)
                .collect(toList());
        return new Issues(filteredIssues);
    }

    private boolean isIncluded(Issue issue) {
        return areAllIncluded() || includeFilters
                .stream()
                .anyMatch(filter -> filter.filter(issue));
    }

    private boolean areAllIncluded() {
        return includeFilters.isEmpty();
    }

    private boolean isExcluded(Issue issue) {
        return excludeFilters
                .stream()
                .noneMatch(filter -> filter.filter(issue));
    }

    /**
     * An extractor for a {@link String string property} of an {@link Issue issue}.
     */
    @FunctionalInterface
    private interface IssuePropertyExtractor {
        /**
         * Extract the property from an {@link Issue issue}.
         *
         * @param issue
         *         the {@link Issue issue} which the property should be extracted from
         *
         * @return the extracted {@link String string property}
         */
        String extract(final Issue issue);
    }

    /**
     * A filter for a certain {@link IssuePropertyExtractor}.
     */
    @FunctionalInterface
    private interface IssuePropertyFilter {
        boolean filter(final Issue issue);
    }

    /**
     * Create a new {@link Builder builder} for an {@link IssueFilter issue filter}.
     *
     * @return the newly created {@link IssueFilter issue filter}
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Properties of an {@link Issue issue} that can be used for filtering.
     */
    public enum IssueProperty {
        FILE_NAME(Issue::getFileName),
        PACKAGE_NAME(Issue::getPackageName),
        MODULE_NAME(Issue::getModuleName),
        CATEGORY(Issue::getCategory),
        TYPE(Issue::getType);

        private final IssuePropertyExtractor extractor;

        IssueProperty(final IssuePropertyExtractor extractor) {
            this.extractor = extractor;
        }
    }

    /**
     * A builder for {@link IssueFilter issue filters}.
     */
    public static class Builder {
        private List<IssuePropertyFilter> includeFilters;
        private List<IssuePropertyFilter> excludeFilters;

        private Builder() {
            this.includeFilters = newArrayList();
            this.excludeFilters = newArrayList();
        }

        private IssuePropertyFilter filter(final IssuePropertyExtractor propertyExtractor, final String regex) {
            Pattern pattern = compile(regex);
            return issue -> pattern.matcher(propertyExtractor.extract(issue)).matches();
        }

        private List<IssuePropertyFilter> filters(final IssuePropertyExtractor propertyExtractor, final List<String> regex) {
            return regex.stream()
                    .map(r -> filter(propertyExtractor, r))
                    .collect(toList());
        }

        /**
         * Add include filters for a given {@link IssueProperty issue property}.
         *
         * @param property
         *         the {@link IssueProperty issue property} which is used for filtering
         * @param regex
         *         the regex to be applied on the specified {@link IssueProperty issue property}
         *
         * @return this {@link Builder builder}
         */
        public Builder include(final IssueProperty property, final List<String> regex) {
            includeFilters.addAll(filters(property.extractor, regex));
            return this;
        }

        /**
         * Add exclude filters for a given {@link IssueProperty issue property}.
         *
         * @param property
         *         the {@link IssueProperty issue property} which is used for filtering
         * @param regex
         *         the regex to be applied on the specified {@link IssueProperty issue property}
         *
         * @return this {@link Builder builder}
         */
        public Builder exclude(final IssueProperty property, final List<String> regex) {
            excludeFilters.addAll(filters(property.extractor, regex));
            return this;
        }

        /**
         * Build a new {@link IssueFilter issue filter} with the provided parameters.
         *
         * @return the newly created {@link IssueFilter issue filter}
         */
        public IssueFilter build() {
            return new IssueFilter(includeFilters, excludeFilters);
        }
    }
}