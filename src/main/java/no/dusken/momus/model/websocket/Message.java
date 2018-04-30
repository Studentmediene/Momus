package no.dusken.momus.model.websocket;

import java.time.ZonedDateTime;

import no.dusken.momus.model.websocket.Action;

public class Message {
	private Long pageId;
	private Long articleId;
	private Long advertId;
	private Action action;
	private String editedField;
	private ZonedDateTime date;

	public Action getAction() {
		return this.action;
    }

	public void setAction(Action action) {
		this.action = action;
	}

	public ZonedDateTime getDate() {
		return date;
	}

	public Long getPageId() {
		return pageId;
	}

	public void setPageId(Long pageId) {
		this.pageId = pageId;
	}

	public Long getAdvertId() { return advertId; }

	public void setAdvertId(Long advertId) { this.advertId = advertId; }

	public Long getArticleId() {
		return articleId;
	}

	public void setArticleId(Long articleId) {
		this.articleId = articleId;
	}

	public String getEditedField() {
		return editedField;
	}

	public void setEditedField(String editedField) {
		this.editedField = editedField;
	}
}