package org.junit.runner;

import org.junit.internal.ClassUtil;
import org.junit.runner.manipulation.Filter;

import static org.junit.runner.FilterFactory.FilterNotCreatedException;
import static org.junit.runner.FilterFactory.NoFilterFactoryParams;

/**
 * Extend this class to create a factory that creates a factory that creates a {@link Filter}.
 */
class FilterFactoryFactory {
    /**
     * Creates a {@link Filter}.
     *
     * A filter specification is of the form "package.of.FilterFactory=args-to-filter-factory" or
     * "package.of.FilterFactory".
     *
     * @param filterSpec The filter specification
     * @throws FilterFactoryNotCreatedException
     * @throws FilterNotCreatedException
     */
    public Filter createFilterFromFilterSpec(String filterSpec)
            throws FilterFactoryNotCreatedException {
        FilterFactory filterFactory;
        FilterFactoryParams params;

        if (filterSpec.contains("=")) {
            String[] tuple = filterSpec.split("=", 2);

            String filterFactoryFqcn = tuple[0];
            String args = tuple[1];

            filterFactory = createFilterFactory(filterFactoryFqcn);
            params = filterFactory.parseArgs(args);

            return filterFactory.createFilter(params);
        } else {
            String filterFactoryFqcn = filterSpec;

            return createFilter(filterFactoryFqcn, new NoFilterFactoryParams());
        }
    }

    /**
     * Creates a {@link Filter}.
     *
     * @param filterFactoryClass The class of the {@link FilterFactory}
     * @param params The arguments to the {@link FilterFactory}
     * @throws FilterNotCreatedException
     * @throws FilterFactoryNotCreatedException
     */
    public Filter createFilter(Class<? extends FilterFactory> filterFactoryClass, FilterFactoryParams params)
            throws FilterFactoryNotCreatedException {
        FilterFactory filterFactory = createFilterFactory(filterFactoryClass);

        return filterFactory.createFilter(params);
    }

    /**
     * Creates a {@link Filter}.
     *
     * @param filterFactoryFqcn The fully qualified class name of the {@link FilterFactory}
     * @param params The arguments to the {@link FilterFactory}
     * @throws FilterNotCreatedException
     * @throws FilterFactoryNotCreatedException
     */
    public Filter createFilter(String filterFactoryFqcn, FilterFactoryParams params)
            throws FilterFactoryNotCreatedException {
        FilterFactory filterFactory = createFilterFactory(filterFactoryFqcn);

        return filterFactory.createFilter(params);
    }

    FilterFactory createFilterFactory(String filterFactoryFqcn) throws FilterFactoryNotCreatedException {
        Class<? extends FilterFactory> filterFactoryClass;

        try {
            filterFactoryClass = ClassUtil.getClass(filterFactoryFqcn).asSubclass(FilterFactory.class);
        } catch (Exception e) {
            throw new FilterFactoryNotCreatedException(e.getMessage());
        }

        return createFilterFactory(filterFactoryClass);
    }

    FilterFactory createFilterFactory(Class<? extends FilterFactory> filterFactoryClass)
            throws FilterFactoryNotCreatedException {
        try {
            return filterFactoryClass
                    .getConstructor()
                    .newInstance();
        } catch (Exception e) {
            throw new FilterFactoryNotCreatedException(e.getMessage());
        }
    }

    /**
     * Exception thrown if the {@link FilterFactory} cannot be created.
     */
    public static class FilterFactoryNotCreatedException extends ClassNotFoundException {
        public FilterFactoryNotCreatedException(String message) {
            super(message);
        }
    }
}
