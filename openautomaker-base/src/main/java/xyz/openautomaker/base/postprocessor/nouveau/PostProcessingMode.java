package xyz.openautomaker.base.postprocessor.nouveau;

/**
 *
 * @author Ian
 */
public enum PostProcessingMode
{
    TASK_BASED_NOZZLE_SELECTION,
    SUPPORT_IN_FIRST_MATERIAL,
    SUPPORT_IN_SECOND_MATERIAL,
    FORCED_USE_OF_E_EXTRUDER,
    FORCED_USE_OF_D_EXTRUDER,
    NO_AVAILABLE_EXTRUDERS,
    LEAVE_TOOL_CHANGES_ALONE_DUAL,
    LEAVE_TOOL_CHANGES_ALONE_SINGLE
}
