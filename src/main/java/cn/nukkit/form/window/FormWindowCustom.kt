package cn.nukkit.form.window

import cn.nukkit.form.element.*

class FormWindowCustom(title: String, contents: List<Element>, icon: ElementButtonImageData) : FormWindow() {
    private val type = "custom_form" //This variable is used for JSON import operations. Do NOT delete :) -- @Snake1999
    var title = ""
    private var icon: ElementButtonImageData
    private val content: List<Element>
    private override var response: FormResponseCustom? = null

    constructor(title: String?) : this(title, ArrayList()) {}
    constructor(title: String?, contents: List<Element?>?) : this(title, contents, null as ElementButtonImageData?) {}
    constructor(title: String?, contents: List<Element?>?, icon: String) : this(title, contents, if (icon.isEmpty()) null else ElementButtonImageData(ElementButtonImageData.IMAGE_DATA_TYPE_URL, icon)) {}

    val elements: List<cn.nukkit.form.element.Element>
        get() = content

    fun addElement(element: Element?) {
        content.add(element)
    }

    fun getIcon(): ElementButtonImageData {
        return icon
    }

    fun setIcon(icon: String) {
        if (!icon.isEmpty()) this.icon = ElementButtonImageData(ElementButtonImageData.IMAGE_DATA_TYPE_URL, icon)
    }

    fun setIcon(icon: ElementButtonImageData) {
        this.icon = icon
    }

    fun getResponse(): FormResponseCustom? {
        return response
    }

    fun setResponse(data: String) {
        if (data.equals("null")) {
            this.closed = true
            return
        }
        val elementResponses: List<String> = Gson().fromJson(data, object : TypeToken<List<String?>?>() {}.getType())
        //elementResponses.remove(elementResponses.size() - 1); //submit button //maybe mojang removed that?
        var i = 0
        val dropdownResponses: HashMap<Integer, FormResponseData> = HashMap()
        val inputResponses: HashMap<Integer, String> = HashMap()
        val sliderResponses: HashMap<Integer, Float> = HashMap()
        val stepSliderResponses: HashMap<Integer, FormResponseData> = HashMap()
        val toggleResponses: HashMap<Integer, Boolean> = HashMap()
        val responses: HashMap<Integer, Object> = HashMap()
        val labelResponses: HashMap<Integer, String> = HashMap()
        for (elementData in elementResponses) {
            if (i >= content.size()) {
                break
            }
            val e: Element = content[i] ?: break
            if (e is ElementLabel) {
                labelResponses.put(i, e.getText())
                responses.put(i, e.getText())
            } else if (e is ElementDropdown) {
                val answer: String = e.getOptions().get(Integer.parseInt(elementData))
                dropdownResponses.put(i, FormResponseData(Integer.parseInt(elementData), answer))
                responses.put(i, answer)
            } else if (e is ElementInput) {
                inputResponses.put(i, elementData)
                responses.put(i, elementData)
            } else if (e is ElementSlider) {
                val answer: Float = Float.parseFloat(elementData)
                sliderResponses.put(i, answer)
                responses.put(i, answer)
            } else if (e is ElementStepSlider) {
                val answer: String = e.getSteps().get(Integer.parseInt(elementData))
                stepSliderResponses.put(i, FormResponseData(Integer.parseInt(elementData), answer))
                responses.put(i, answer)
            } else if (e is ElementToggle) {
                val answer: Boolean = Boolean.parseBoolean(elementData)
                toggleResponses.put(i, answer)
                responses.put(i, answer)
            }
            i++
        }
        response = FormResponseCustom(responses, dropdownResponses, inputResponses,
                sliderResponses, stepSliderResponses, toggleResponses, labelResponses)
    }

    /**
     * Set Elements from Response
     * Used on ServerSettings Form Response. After players set settings, we need to sync these settings to the server.
     */
    fun setElementsFromResponse() {
        if (response != null) {
            response.getResponses().forEach { i, response ->
                val e: Element = content[i]
                if (e != null) {
                    if (e is ElementDropdown) {
                        e.setDefaultOptionIndex(e.getOptions().indexOf(response))
                    } else if (e is ElementInput) {
                        e.setDefaultText(response as String)
                    } else if (e is ElementSlider) {
                        e.setDefaultValue(response as Float)
                    } else if (e is ElementStepSlider) {
                        e.setDefaultOptionIndex(e.getSteps().indexOf(response))
                    } else if (e is ElementToggle) {
                        e.setDefaultValue(response as Boolean)
                    }
                }
            }
        }
    }

    init {
        this.title = title
        content = contents
        this.icon = icon
    }
}