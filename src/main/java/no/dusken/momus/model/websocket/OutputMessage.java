package no.dusken.momus.model.websocket;

import java.time.ZonedDateTime;

public class OutputMessage {
	private Long pageId;
	private Long articleId;
	private Long advertId;
	private Action action;
	private String editedField;
	private ZonedDateTime date;

    public OutputMessage(Long pageId, Long articleId, Long advertId, Action action, String editedField, ZonedDateTime date){
        this.pageId = pageId;
        this.articleId = articleId;
        this.advertId = advertId;
		this.action = action;
		this.editedField = editedField;
        this.date = date;
    }

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

	public Long getArticleId() {
		return articleId;
	}

	public void setArticleId(Long articleId) {
		this.articleId = articleId;
	}

	public Long getAdvertId(){ return advertId; }

	public void setAdvertId(Long advertId){ this.advertId = advertId; }

	public String getEditedField() {
		return editedField;
	}

	public void setEditedField(String editedField) {
		this.editedField = editedField;
	}
}