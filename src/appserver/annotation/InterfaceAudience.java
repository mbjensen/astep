package dk.aau.astep.appserver.annotation;

import java.lang.annotation.Documented;

@InterfaceAudience.Public
public class InterfaceAudience {
    private InterfaceAudience() {}

    /**
     * The annotated element is intended for use by any component
     */
    @Documented
    public @interface Public {}

    /**
     * The annotated element is only intended for use within the component
     */
    @Documented
    public @interface Private {}
}


