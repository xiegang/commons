package my.commons.web.taglibs.ui;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import my.commons.Constants;
import my.commons.db.pagination.Pagination;
import my.commons.web.util.ServletUtils;

import org.apache.commons.lang3.StringUtils;

import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * 分页标签<br/>
 * <ul>
 * 	<li>model</li>
 * 	<li>theme</li>
 * 	<li>encoding</li>
 * </ul>
 * 
 * <ul>
 * 	<li>url</li>
 * <li>excludeParams</li>
 * 	<li>page 当前页数</li>
 * 	<li>totalPage 总页数</li>
 * 	<li>pageSize 每一页数据量</li>
 * 	<li>rowCount 总数据量</li>
 * 	<li>prefix生成连接的参数前缀</li>
 * </ul>
 * @author xiegang
 * @since 2013-1-28
 *
 */
public class PaginationTag extends FreemarkerTagSupport {
	private static final long serialVersionUID = -7587965663988400795L;
	
	/** 默认分页样式 */
	public static String DEFAULT_THEME = "pagination.ftl";
	
	/** 默认编码 */
	public static String DEFAULT_ENCODING = Constants.ENCODING;

	/**
	 * 路径
	 */
	private String url;
	
	/** 自动获取url时排除的参数 */
	private String excludeParams = Constants.PAGINATION_PARAMETERS;
	
	/**
	 * 分页模型
	 */
	private Pagination model;
	
	/**
	 * 主题
	 */
	private String theme;
	
	/**
	 * 编码
	 */
	private String encoding;
	
	/**
	 * 生成链接的分页参数的前缀
	 */
	private String prefix;
	
	public PaginationTag() {
		super();
		this.model = new Pagination();
		this.encoding = DEFAULT_ENCODING;
		this.theme = DEFAULT_THEME;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.jsp.tagext.TagSupport#doEndTag()
	 */
	@Override
	public int doEndTag() throws JspException {
		Template template = null;
		try {
			// get default url
			if(null == this.url || StringUtils.isBlank(this.url)) {
				this.url = ServletUtils.getRequestURI( (HttpServletRequest) this.pageContext.getRequest(),
						my.commons.lang.util.StringUtils.trim(this.excludeParams.split(",")));
			}
						
			try {
				template = this.freemarkerConfiguration.getTemplate(this.theme, this.encoding);
			} catch (Exception e) {
				template = this.freemarkerConfiguration.getTemplate(DEFAULT_THEME, this.encoding);
			}
		
			if(model != null && null != template) {
				JspWriter out = pageContext.getOut();
				Map<String, Object> rootMap = ftlMapWrapper();
				template.process(rootMap, out);
				out.flush();
			}
		} catch (IOException e) {
			throw new JspException("PaginationTag IOException", e);
		} catch (TemplateException e) {
			throw new JspException("PaginationTag TemplateException", e);
		}
		return EVAL_PAGE;
	}

	private Map<String, Object> ftlMapWrapper() {
		Map<String, Object> rootMap = new HashMap<String, Object>();
		// url
		rootMap.put("url", this.url);
		// 当前页数
		rootMap.put("page", this.model.getCurrentPage());
		// 总页数
		rootMap.put("totalPage", this.model.getTotalPage());
		// 每一页数据量
		rootMap.put("pageSize", this.model.getPageSize());
		// 总数据量
		rootMap.put("rowCount", this.model.getRowCount());
		// 生成连接的参数前缀
		rootMap.put("prefix", this.prefix);
		return rootMap;
	}

	public void setModel(Pagination model) {
		this.model = model;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	/**
	 * 当前页数
	 * @param page
	 */
	public void setPage(int page) {
		this.model.setCurrentPage(page);
	}
	
	/**
	 * 每一页数据量
	 * @param pageSize
	 */
	public void setPageSize(int pageSize) {
		this.model.setPageSize(pageSize);
	}

	public void setRowCount(int rowCount) {
		this.model.setRowCount(rowCount);
	}
	
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public void setTheme(String theme) {
		this.theme = theme + ".ftl";
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	
	public void setExcludeParams(String excludeParams) {
		this.excludeParams = excludeParams;
	}
}
