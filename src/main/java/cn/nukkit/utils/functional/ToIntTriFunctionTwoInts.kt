package cn.nukkit.utils.functional

import cn.nukkit.api.PowerNukkitOnly

/**
 * Represents a function that accepts three arguments where the last two are int and produces an int result.
 * This is the three-arity specialization of [Function].
 *
 *
 * This is a functional interface
 * whose functional method is [.apply].
 *
 * @param <F> the type of the first argument to the function
 *
 * @see Function
 *
 * @since 1.4.0.0-PN
</F> */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
@FunctionalInterface
interface ToIntTriFunctionTwoInts<F> {
    /**
     * Applies this function to the given arguments.
     *
     * @param f the first function argument
     * @param s the second function argument
     * @param t the third function argument
     * @return the function result
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun apply(f: F, s: Int, t: Int): Int

    /**
     * Returns a composed function that first applies this function to
     * its input, and then applies the `after` function to the result.
     * If evaluation of either function throws an exception, it is relayed to
     * the caller of the composed function.
     *
     * @param after the function to apply after this function is applied
     * @return a composed function that first applies this function and then
     * applies the `after` function
     * @throws NullPointerException if after is null
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun andThen(after: IntUnaryOperator): ToIntTriFunctionTwoInts<F>? {
        Objects.requireNonNull(after)
        return ToIntTriFunctionTwoInts<F> { f: F, s: Int, t: Int -> after.applyAsInt(apply(f, s, t)) }
    }
}