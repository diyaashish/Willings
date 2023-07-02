package jp.loioz.common.utility;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import org.apache.commons.collections4.Predicate;

import lombok.NoArgsConstructor;

/**
 * コレクション用のUtilクラス
 */
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class LoiozCollectionUtils {

	/**
	 * 2つのリストの、同じインデックスの要素同士を処理する。<br>
	 * 各リストの要素数が異なる場合は、要素数が一番多いリストに合わせてループして、不足する要素はnullとしてコールバック関数に渡される。
	 *
	 * @param leftList リスト1
	 * @param rightList リスト2
	 * @param executer 処理内容
	 */
	public static <L, R> void zipping(
			List<? extends L> leftList,
			List<? extends R> rightList,
			BiConsumer<? super L, ? super R> executer) {
		zipping(leftList, null, rightList,
				(left) -> (center) -> (right) -> executer.accept(left, right));
	}

	/**
	 * 3つのリストの、同じインデックスの要素同士を処理する。<br>
	 * 各リストの要素数が異なる場合は、要素数が一番多いリストに合わせてループして、不足する要素はnullとしてコールバック関数に渡される。
	 *
	 * @param leftList リスト1
	 * @param centerList リスト2
	 * @param rightList リスト3
	 * @param executer 処理内容
	 */
	public static <L, C, R> void zipping(
			List<? extends L> leftList,
			List<? extends C> centerList,
			List<? extends R> rightList,
			Function<? super L, Function<? super C, Consumer<? super R>>> executer) {

		Iterator<? extends L> leftIterator = leftList != null ? leftList.iterator() : Collections.emptyIterator();
		Iterator<? extends C> centerIterator = centerList != null ? centerList.iterator() : Collections.emptyIterator();
		Iterator<? extends R> rightIterator = rightList != null ? rightList.iterator() : Collections.emptyIterator();

		while (leftIterator.hasNext() || centerIterator.hasNext() || rightIterator.hasNext()) {
			L left = leftIterator.hasNext() ? leftIterator.next() : null;
			C center = centerIterator.hasNext() ? centerIterator.next() : null;
			R right = rightIterator.hasNext() ? rightIterator.next() : null;

			executer.apply(left).apply(center).accept(right);
		}
	}

	/**
	 * 2つのリストの、同じインデックスの要素同士を結合する。<br>
	 * 各リストの要素数が異なる場合は、要素数が一番多いリストに合わせてループして、不足する要素はnullとしてコールバック関数に渡される。
	 *
	 * @param leftList リスト1
	 * @param rightList リスト2
	 * @param zipper 結合関数
	 * @return 結合結果
	 */
	public static <L, R, T> List<T> zip(
			List<? extends L> leftList,
			List<? extends R> rightList,
			BiFunction<? super L, ? super R, ? extends T> zipper) {
		return zip(leftList, null, rightList,
				(left) -> (center) -> (right) -> zipper.apply(left, right));
	}

	/**
	 * 3つのリストの、同じインデックスの要素同士を結合する。<br>
	 * 各リストの要素数が異なる場合は、要素数が一番多いリストに合わせてループして、不足する要素はnullとしてコールバック関数に渡される。
	 *
	 * @param leftList リスト1
	 * @param centerList リスト2
	 * @param rightList リスト3
	 * @param zipper 結合関数
	 * @return 結合結果
	 */
	public static <L, C, R, T> List<T> zip(
			List<? extends L> leftList,
			List<? extends C> centerList,
			List<? extends R> rightList,
			Function<? super L, Function<? super C, Function<? super R, T>>> zipper) {

		List<T> result = new ArrayList<>();
		zipping(leftList, centerList, rightList, (left) -> (center) -> (right) -> {
			result.add(zipper.apply(left).apply(center).apply(right));
		});

		return result;
	}

	/**
	 * マージしたListを返却します
	 * 
	 * @param list1
	 * @param list2
	 * @return マージしたlist
	 */
	public static <T> List<T> mergeLists(List<T> list1, List<T> list2) {
		List<T> merged = new LinkedList<T>();
		if (list1 != null) {
			merged.addAll(list1);
		}
		if (list2 != null) {
			merged.addAll(list2);
		}
		return merged;
	}

	/**
	 * org.apache.commons.collections4.CollectionUtils.isEmpty(final Collection<?> coll)
	 * 
	 * @param coll
	 * @return
	 */
	public static boolean isNotEmpty(Collection<?> coll) {
		return org.apache.commons.collections4.CollectionUtils.isNotEmpty(coll);
	}

	/**
	 * org.apache.commons.collections4.CollectionUtils.isEmpty
	 * 
	 * @param coll
	 * @return
	 */
	public static boolean isEmpty(Collection<?> coll) {
		return org.apache.commons.collections4.CollectionUtils.isEmpty(coll);
	}

	/**
	 * org.apache.commons.collections4.CollectionUtils.subtract(a, b);
	 * 
	 * @param <O>
	 * @param a
	 * @param b
	 * @return
	 */
	public static <O> Collection<O> subtract(final Iterable<? extends O> a, final Iterable<? extends O> b) {
		return org.apache.commons.collections4.CollectionUtils.subtract(a, b);
	}

	/**
	 * org.apache.commons.collections4.CollectionUtils.select(final Iterable<? extends O> inputCollection, final Predicate<? super O> predicate, final R outputCollection)
	 * 
	 * @param <O>
	 * @param <R>
	 * @param inputCollection
	 * @param predicate
	 * @param outputCollection
	 * @return
	 */
	public static <O, R extends Collection<? super O>> R select(final Iterable<? extends O> inputCollection, final Predicate<? super O> predicate, final R outputCollection) {
		return org.apache.commons.collections4.CollectionUtils.select(inputCollection, predicate, outputCollection);
	}

	/**
	 * org.apache.commons.collections4.CollectionUtils.intersection(final Iterable<? extends O> a, final Iterable<? extends O> b)
	 * 
	 * @param <O>
	 * @param a
	 * @param b
	 * @return
	 */
	public static <O> Collection<O> intersection(final Iterable<? extends O> a, final Iterable<? extends O> b) {
		return org.apache.commons.collections4.CollectionUtils.intersection(a, b);
	}
}
