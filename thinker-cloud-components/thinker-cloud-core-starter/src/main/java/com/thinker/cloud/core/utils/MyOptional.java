package com.thinker.cloud.core.utils;


import lombok.Data;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 自定义Optional工具类
 * <p>
 * 主要为了扩展Optional中不满足的处理逻辑
 *
 * @author admin
 **/
@Data
public final class MyOptional<T> {

    /**
     * Common instance for {@code empty()}.
     */
    private static final MyOptional<?> EMPTY = new MyOptional<>();

    /**
     * If non-null, the value; if null, indicates no value is present
     */
    private final T value;

    /**
     * Constructs an empty instance.
     *
     * @implNote Generally only one empty instance, {@link MyOptional#EMPTY},
     * should exist per VM.
     */
    private MyOptional() {
        this.value = null;
    }

    /**
     * Constructs an instance with the value present.
     *
     * @param value the non-null value to be present
     * @throws NullPointerException if value is null
     */
    private MyOptional(T value) {
        this.value = Objects.requireNonNull(value);
    }

    /**
     * Returns an empty {@code Optional} instance.  No value is present for this
     * Optional.
     *
     * @param <T> Type of the non-existent value
     * @return an empty {@code Optional}
     * @apiNote Though it may be tempting to do so, avoid testing if an object
     * is empty by comparing with {@code ==} against instances returned by
     * {@code Option.empty()}. There is no guarantee that it is a singleton.
     * Instead, use {@link #isPresent()}.
     */
    @SuppressWarnings("unchecked")
    public static <T> MyOptional<T> empty() {
        return (MyOptional<T>) EMPTY;
    }

    /**
     * Returns an {@code Optional} describing the given value, if
     * non-{@code null}, otherwise returns an empty {@code Optional}.
     *
     * @param value the possibly-{@code null} value to describe
     * @param <T>   the type of the value
     * @return an {@code Optional} with a present value if the specified value
     * is non-{@code null}, otherwise an empty {@code Optional}
     */
    @SuppressWarnings("unchecked")
    public static <T> MyOptional<T> ofNullable(T value) {
        return value == null ? (MyOptional<T>) EMPTY : new MyOptional<>(value);
    }

    /**
     * If a value is present, apply the provided mapping function to it,
     * and if the result is non-null, return an {@code Optional} describing the
     * result.  Otherwise return an empty {@code Optional}.
     *
     * @param <U>    The type of the result of the mapping function
     * @param mapper a mapping function to apply to the value, if present
     * @return an {@code Optional} describing the result of applying a mapping
     * function to the value of this {@code Optional}, if a value is present,
     * otherwise an empty {@code Optional}
     * @throws NullPointerException if the mapping function is null
     * @apiNote This method supports post-processing on optional values, without
     * the need to explicitly check for a return status.  For example, the
     * following code traverses a stream of file names, selects one that has
     * not yet been processed, and then opens that file, returning an
     * {@code Optional<FileInputStream>}:
     *
     * <pre>{@code
     *     Optional<FileInputStream> fis =
     *         names.stream().filter(name -> !isProcessedYet(name))
     *                       .findFirst()
     *                       .map(name -> new FileInputStream(name));
     * }</pre>
     * <p>
     * Here, {@code findFirst} returns an {@code Optional<String>}, and then
     * {@code map} returns an {@code Optional<FileInputStream>} for the desired
     * file if one exists.
     */
    public <U> MyOptional<U> map(Function<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper);
        if (!isPresent()) {
            return empty();
        } else {
            return MyOptional.ofNullable(mapper.apply(value));
        }
    }

    /**
     * If a value is null, otherwise throws an exception
     * produced by the exception supplying function.
     *
     * @param <X>               Type of the exception to be thrown
     * @param exceptionSupplier the supplying function that produces an exception to be thrown
     * @throws X if value is null
     * @apiNote A method reference to the exception constructor with an empty argument
     * list can be used as the supplier. For example,
     */
    public <X extends Throwable> void orEmptyThrow(Supplier<? extends X> exceptionSupplier) throws X {
        if (value == null) {
            throw exceptionSupplier.get();
        }
    }

    /**
     * If a value is not null, otherwise throws an exception
     * produced by the exception supplying function.
     *
     * @param <X>               Type of the exception to be thrown
     * @param exceptionSupplier the supplying function that produces an exception to be thrown
     * @throws X if value is not null
     * @apiNote A method reference to the exception constructor with an empty argument
     * list can be used as the supplier. For example,
     */
    public <X extends Throwable> void orNonEmptyThrow(Supplier<? extends X> exceptionSupplier) throws X {
        if (value != null) {
            throw exceptionSupplier.get();
        }
    }

    /**
     * Return {@code true} if there is a value present, otherwise {@code false}.
     *
     * @return {@code true} if there is a value present, otherwise {@code false}
     */
    public boolean isPresent() {
        return value != null;
    }

    /**
     * Returns the equivalent {@code java.util.Optional} value to this optional.
     *
     * <p>Unfortunately, the method reference {@code Optional::toJavaUtil} will not work, because it
     * could refer to either the static or instance version of this method. Write out the lambda
     * expression {@code o -> o.toJavaUtil()} instead.
     *
     * @since 21.0
     */
    public Optional<T> toJavaOptional() {
        return Optional.ofNullable(value);
    }
}
