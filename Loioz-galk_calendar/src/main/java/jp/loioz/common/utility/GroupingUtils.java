package jp.loioz.common.utility;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import jp.loioz.common.utility.data.GroupParent;
import lombok.NoArgsConstructor;

/**
 * データをグループ化するUtilクラス
 */
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class GroupingUtils {

	/**
	 * 複数要素をグループ化して変換する関数
	 *
	 * @param <S> SOURCE:変換元クラス
	 * @param <T> TYPE:変換後クラス
	 */
	public static interface GroupMapper<S, T> extends Function<List<S>, List<T>> {
		default List<T> create(List<S> source) {
			return apply(source);
		}
	}

	/**
	 * グループ化変換の親子関係
	 *
	 * @param <S> SOURCE:変換元クラス
	 * @param <C> CHILD:子要素クラス
	 * @param <P> PARENT:親要素クラス
	 */
	public static interface MapperChain<S, C, P> extends Function<GroupMapper<S, C>, GroupMapper<S, P>> {

		/**
		 * 子要素の変換ルールを追加する
		 *
		 * @param <GC> GRAND_CHILD:孫要素クラス
		 * @param childMapper 子要素変換ルール
		 * @param childClassifier 子要素グループ化キー
		 * @return 孫要素から親要素へのグループ化変換ルール
		 */
		@SuppressWarnings("unchecked")
		default <GC> MapperChain<S, GC, P> addMapping(
				Function<S, ? extends GroupParent<GC>> childMapper,
				Function<S, Object> childClassifier) {

			MapperChain<S, GC, C> childMapperChain = (MapperChain<S, GC, C>) mapperChain(childMapper, childClassifier);

			return (GroupMapper<S, GC> gcMapper) -> compose(childMapperChain).apply(gcMapper);
		}

		/**
		 * 子要素の変換ルールを追加する(子要素のグループ化なし)
		 *
		 * @param childMapper 子要素変換ルール
		 * @return 親要素のグループ化変換ルール
		 */
		default GroupMapper<S, P> addMapping(Function<S, C> childMapper) {
			return apply(groupMapper(childMapper));
		}
	}

	/**
	 * 変換ルールを追加する
	 *
	 * @param <S> SOURCE:変換元クラス
	 * @param <P> PARENT:変換後クラス
	 * @param <C> CHILD:変換後クラスの子要素クラス
	 * @param mapper 変換ルール
	 * @param classifier グループ化キー
	 * @return 変換ルール
	 */
	public static <S, P extends GroupParent<C>, C> MapperChain<S, C, P> addMapping(
			Function<S, P> mapper,
			Function<S, Object> classifier) {
		return mapperChain(mapper, classifier);
	}

	/**
	 * 変換ルールを追加する
	 *
	 * @param <S> SOURCE:変換元クラス
	 * @param <P> PARENT:変換後クラス
	 * @param <C> CHILD:変換後クラスの子要素クラス
	 * @param parentMapper 親要素変換ルール
	 * @param parentClassifier 親要素グループ化キー
	 * @return 変換ルール
	 */
	private static <S, P extends GroupParent<C>, C> MapperChain<S, C, P> mapperChain(
			Function<S, P> parentMapper,
			Function<S, Object> parentClassifier) {
		return childGroupMapper -> groupMapper(parentMapper, parentClassifier, childGroupMapper);
	}

	/**
	 * グループ化変換ルールを取得する
	 *
	 * @param <S> SOURCE:変換元クラス
	 * @param <P> PARENT:変換後クラス
	 * @param <C> CHILD:変換後クラスの子要素クラス
	 * @param parentMapper 親要素変換ルール
	 * @param parentClassifier 親要素グループ化キー
	 * @param childGroupMapper 子要素グループ変換ルール
	 * @return グループ化変換ルール
	 */
	private static <S, P extends GroupParent<C>, C> GroupMapper<S, P> groupMapper(
			Function<S, P> parentMapper,
			Function<S, Object> parentClassifier,
			GroupMapper<S, C> childGroupMapper) {
		return source -> create(source, parentMapper, parentClassifier, childGroupMapper);
	}

	/**
	 * グループ化変換ルールを取得する(子要素のグループ化なし)
	 *
	 * @param <S> SOURCE:変換元クラス
	 * @param <T> TYPE:変換後クラス
	 * @param mapper 変換ルール
	 * @return グループ化変換ルール
	 */
	private static <S, T> GroupMapper<S, T> groupMapper(
			Function<S, T> mapper) {
		return source -> source.stream()
				.map(mapper)
				.collect(Collectors.toList());
	}

	/**
	 * 変換元情報をグループ化して変換する
	 *
	 * @param <S> SOURCE:変換元クラス
	 * @param <P> PARENT:変換後クラス
	 * @param <C> CHILD:変換後クラスの子要素クラス
	 * @param source 変換元情報
	 * @param parentMapper 親要素変換ルール
	 * @param parentClassifier 親要素グループ化キー
	 * @param childGroupMapper 子要素グループ変換ルール
	 * @return 変換結果
	 */
	private static <S, P extends GroupParent<C>, C> List<P> create(
			List<S> source,
			Function<S, P> parentMapper,
			Function<S, Object> parentClassifier,
			GroupMapper<S, C> childGroupMapper) {

		// 親要素をグループ化キーでグループ化
		LinkedHashMap<Object, List<S>> groupedSourceMap = source.stream()
				.collect(Collectors.groupingBy(
						parentClassifier, LinkedHashMap::new, Collectors.toList()));

		// グループ毎に変換
		List<P> parentList = groupedSourceMap.values().stream()
				.map(groupedSource -> {
					// 親要素を作成
					P item = groupedSource.stream()
							.findFirst()
							.map(parentMapper)
							.get();

					// 子要素をグループ化して作成
					List<C> children = childGroupMapper.apply(groupedSource);
					item.setChildren(children);

					return item;
				})
				.collect(Collectors.toList());

		return parentList;
	}

}
