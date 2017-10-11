package no.dusken.momus.model.websocket;

import java.util.Date;
import no.dusken.momus.model.websocket.Action;

public class Message {
	private Long pageId;
	private Long articleId;
    private Action action;
    private Date date;

	public Action getAction() {
		return this.action;
    }

	public void setAction(Action action) {
		this.action = action;
	}

	public Date getDate() {
		return date;
	}

	public Long getPageId() {
		return pageId;
	}

	public void setPageId(Long pageId) {
		this.pageId = pageId;
	}

	public Long getArticleId() {
		return articleId;
	}

	public void setArticleId(Long articleId) {
		this.articleId = articleId;
	}
}