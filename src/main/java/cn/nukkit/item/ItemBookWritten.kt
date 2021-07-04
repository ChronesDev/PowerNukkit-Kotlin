package cn.nukkit.item

import cn.nukkit.nbt.tag.CompoundTag

class ItemBookWritten @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : ItemBookWritable(Item.WRITTEN_BOOK, 0, count, "Written Book") {
    constructor(meta: Integer?) : this(meta, 1) {}

    @Override
    override fun getMaxStackSize(): Int {
        return 16
    }

    fun writeBook(author: String?, title: String?, pages: Array<String?>): Item {
        val pageList: ListTag<CompoundTag> = ListTag("pages")
        for (page in pages) {
            pageList.add(createPageTag(page))
        }
        return writeBook(author, title, pageList)
    }

    fun writeBook(author: String?, title: String?, pages: ListTag<CompoundTag?>): Item {
        if (pages.size() > 50 || pages.size() <= 0) return this //Minecraft does not support more than 50 pages
        val tag: CompoundTag = if (this.hasCompoundTag()) this.getNamedTag() else CompoundTag()
        tag.putString("author", author)
        tag.putString("title", title)
        tag.putList(pages)
        tag.putInt("generation", GENERATION_ORIGINAL)
        tag.putString("xuid", "")
        return this.setNamedTag(tag)
    }

    fun signBook(title: String?, author: String?, xuid: String?, generation: Int): Boolean {
        this.setNamedTag((if (this.hasCompoundTag()) this.getNamedTag() else CompoundTag())
                .putString("title", title)
                .putString("author", author)
                .putInt("generation", generation)
                .putString("xuid", xuid))
        return true
    }

    /**
     * Returns the generation of the book.
     * Generations higher than 1 can not be copied.
     */
    fun getGeneration(): Int {
        return if (this.hasCompoundTag()) this.getNamedTag().getInt("generation") else -1
    }

    /**
     * Sets the generation of a book.
     */
    fun setGeneration(generation: Int) {
        this.setNamedTag((if (this.hasCompoundTag()) this.getNamedTag() else CompoundTag()).putInt("generation", generation))
    }

    /**
     * Returns the author of this book.
     * This is not a reliable way to get the name of the player who signed this book.
     * The author can be set to anything when signing a book.
     */
    fun getAuthor(): String {
        return if (this.hasCompoundTag()) this.getNamedTag().getString("author") else ""
    }

    /**
     * Sets the author of this book.
     */
    fun setAuthor(author: String?) {
        this.setNamedTag((if (this.hasCompoundTag()) this.getNamedTag() else CompoundTag()).putString("author", author))
    }

    /**
     * Returns the title of this book.
     */
    fun getTitle(): String {
        return if (this.hasCompoundTag()) this.getNamedTag().getString("title") else "Written Book"
    }

    /**
     * Sets the title of this book.
     */
    fun setTitle(title: String?) {
        this.setNamedTag((if (this.hasCompoundTag()) this.getNamedTag() else CompoundTag()).putString("title", title))
    }

    /**
     * Returns the author's XUID of this book.
     */
    fun getXUID(): String {
        return if (this.hasCompoundTag()) this.getNamedTag().getString("xuid") else ""
    }

    /**
     * Sets the author's XUID of this book.
     */
    fun setXUID(title: String?) {
        this.setNamedTag((if (this.hasCompoundTag()) this.getNamedTag() else CompoundTag()).putString("xuid", title))
    }

    companion object {
        const val GENERATION_ORIGINAL = 0
        const val GENERATION_COPY = 1
        const val GENERATION_COPY_OF_COPY = 2
        const val GENERATION_TATTERED = 3
    }
}