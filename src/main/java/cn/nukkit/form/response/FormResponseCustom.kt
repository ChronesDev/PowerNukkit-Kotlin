package cn.nukkit.form.response

import java.util.HashMap

class FormResponseCustom(responses: HashMap<Integer?, Object?>, dropdownResponses: HashMap<Integer?, FormResponseData?>,
                         inputResponses: HashMap<Integer?, String?>, sliderResponses: HashMap<Integer?, Float?>,
                         stepSliderResponses: HashMap<Integer?, FormResponseData?>,
                         toggleResponses: HashMap<Integer?, Boolean?>,
                         labelResponses: HashMap<Integer?, String?>) : FormResponse() {
    private val responses: HashMap<Integer, Object>
    private val dropdownResponses: HashMap<Integer, FormResponseData>
    private val inputResponses: HashMap<Integer, String>
    private val sliderResponses: HashMap<Integer, Float>
    private val stepSliderResponses: HashMap<Integer, FormResponseData>
    private val toggleResponses: HashMap<Integer, Boolean>
    private val labelResponses: HashMap<Integer, String>
    fun getResponses(): HashMap<Integer, Object> {
        return responses
    }

    fun getResponse(id: Int): Object {
        return responses.get(id)
    }

    fun getDropdownResponse(id: Int): FormResponseData {
        return dropdownResponses.get(id)
    }

    fun getInputResponse(id: Int): String {
        return inputResponses.get(id)
    }

    fun getSliderResponse(id: Int): Float {
        return sliderResponses.get(id)
    }

    fun getStepSliderResponse(id: Int): FormResponseData {
        return stepSliderResponses.get(id)
    }

    fun getToggleResponse(id: Int): Boolean {
        return toggleResponses.get(id)
    }

    fun getLabelResponse(id: Int): String {
        return labelResponses.get(id)
    }

    init {
        this.responses = responses
        this.dropdownResponses = dropdownResponses
        this.inputResponses = inputResponses
        this.sliderResponses = sliderResponses
        this.stepSliderResponses = stepSliderResponses
        this.toggleResponses = toggleResponses
        this.labelResponses = labelResponses
    }
}