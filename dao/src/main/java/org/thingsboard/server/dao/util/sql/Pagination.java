package org.thingsboard.server.dao.util.sql;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.AbstractPageRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.lang.Nullable;
import org.thingsboard.server.common.data.page.PageLink;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义分页组件，模仿org.springframework.data.domain.PageRequest所写，用于替代PageRequest类生成分页信息
 * 并加入sortStr，作用排序String参数
 * @author Lee
 *
 * @param <E>
 */
public class Pagination<E> extends AbstractPageRequest {
	

	private static final long serialVersionUID = -4541509938956089562L;

	private Sort sort;
	
	private String sortStr;

	/**
	 * Creates a new {@link PageRequest}. Pages are zero indexed, thus providing 0 for {@code page} will return the first
	 * page.
	 *
	 * @param page zero-based page index.
	 * @param size the size of the page to be returned.
	 *  use {@link #of(int, int)} instead.
	 */
	public Pagination(){
		this(0,10);
	}

	public Pagination(SimplePageInfo simplePageInfo){
		this(simplePageInfo.getPage(),simplePageInfo.getSize());
		this.setSortStr(simplePageInfo.getSortStr());
	}
	public Pagination(int page, int size) {
		this(page, size, Sort.unsorted());
	}
	
	public Pagination(int page, int size, String sortStr) {
		this(page, size);
		this.setSortStr(sortStr);
	}


	public Pagination(PageLink  pageLink) {
		this(pageLink.getPage(), pageLink.getPageSize());
		this.setSortStr(sortStr);
	}

	/**
	 * Creates a new {@link PageRequest} with sort parameters applied.
	 *
	 * @param page zero-based page index.
	 * @param size the size of the page to be returned.
	 * @param direction the direction of the {@link Sort} to be specified, can be {@literal null}.
	 * @param properties the properties to sort by, must not be {@literal null} or empty.
	 *  use {@link #of(int, int, Direction, String...)} instead.
	 */
	public Pagination(int page, int size, Direction direction, String... properties) {
		this(page, size, Sort.by(direction, properties));
	}

	/**
	 * Creates a new {@link PageRequest} with sort parameters applied.
	 *
	 * @param page zero-based page index.
	 * @param size the size of the page to be returned.
	 * @param sort can be {@literal null}.
	 *  since 2.0, use {@link #of(int, int, Sort)} instead.
	 */
	public Pagination(int page, int size, Sort sort) {
		super(page, size);
		this.sort = sort;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.domain.Pageable#getSort()
	 */
	public Sort getSort() {
		return sort;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.domain.Pageable#next()
	 */
	public Pageable next() {
		return new Pagination(getPageNumber() + 1, getPageSize(), getSort());
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.domain.AbstractPageRequest#previous()
	 */
	public Pagination previous() {
		return getPageNumber() == 0 ? this : new Pagination(getPageNumber() - 1, getPageSize(), getSort());
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.domain.Pageable#first()
	 */
	public Pageable first() {
		return new Pagination(0, getPageSize(), getSort());
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(@Nullable Object obj) {

		if (this == obj) {
			return true;
		}

		if (!(obj instanceof Pagination)) {
			return false;
		}

		Pagination that = (Pagination) obj;

		return super.equals(that) && this.sort.equals(that.sort);
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return 31 * super.hashCode() + sort.hashCode();
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("Page request [number: %d, size %d, sort: %s]", getPageNumber(), getPageSize(), sort);
	}

	public String getSortStr() {
		return sortStr;
	}

	public void setSortStr(String sortStr) {
		this.sortStr = sortStr;
		this.sort = sortByStr(this.sortStr);
	}

	/**
	 * 根据字符串生成排序对象
	 * @param sortStr： 示例 name1 asc, name2 desc
	 * @return
	 */
	public static Sort sortByStr(String sortStr){
		Sort sort = Sort.unsorted();
		if(StringUtils.isNotBlank(sortStr)){
			String[] sorts = sortStr.split(",");
			List<Order> orderList = new ArrayList<Order>();
			for(String s : sorts){
				String[] o = s.trim().split(" ");
				if(o.length == 2){
					orderList.add(new Order(Direction.fromString(o[1]), o[0]));
				} else {
					orderList.add(Order.asc(o[0]));
				}
			}
			sort = Sort.by(orderList);
		}
		return sort;
	}
	
}
