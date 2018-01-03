package no.dusken.momus.model.websocket;

import java.util.Date;

public class OutputMessage {
	private Long pageId;
	private Long articleId;
	private Action action;
	private String editedField;
    private Date date;

    public OutputMessage(Long pageId, Long articleId, Action action, String editedField, Date date){
        this.pageId = pageId;
        this.articleId = articleId;
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

	public String getEditedField() {
		return editedField;
	}

	public void setEditedField(String editedField) {
		this.editedField = editedField;
	}
}