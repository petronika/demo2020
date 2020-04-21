package ru.galaktika.eim.drools.test.junit4.annotation;

import org.kie.api.io.ResourceType;

/**
 * @author Peter Titov
 */
public enum DroolsResourceType {

    /**	@see {@link ResourceType#DRL} */
    DRL(ResourceType.DRL),

    /**	@see {@link ResourceType#GDRL} */
    GDRL(ResourceType.GDRL),

    /**	@see {@link ResourceType#RDRL} */
    RDRL(ResourceType.RDRL),

    /**	@see {@link ResourceType#XDRL} */
    XDRL(ResourceType.XDRL),

    /**	@see {@link ResourceType#DSL} */
    DSL(ResourceType.DSL),

    /**	@see {@link ResourceType#DSLR} */
    DSLR(ResourceType.DSLR),

    /**	@see {@link ResourceType#RDSLR} */
    RDSLR(ResourceType.RDSLR),

    /**	@see {@link ResourceType#DRF} */
    DRF(ResourceType.DRF),

    /**	@see {@link ResourceType#DTABLE} */
    DTABLE(ResourceType.DTABLE),

    /**	@see {@link ResourceType#BRL} */
    BRL(ResourceType.BRL),

    /**	@see {@link ResourceType#TDRL} */
    TDRL(ResourceType.TDRL),

    /**	@see {@link ResourceType#TEMPLATE} */
    TEMPLATE(ResourceType.TEMPLATE),

    /**	@see {@link ResourceType#DRT} */
    DRT(ResourceType.DRT);

    private final ResourceType resourceType;

    DroolsResourceType(ResourceType resourseType) {
        this.resourceType = resourseType;
    }

    public ResourceType getResourseType() {
        return resourceType;
    }
}