package com.ecyware.android.lottopanama.logic;

public class LottoHtmlContext {

    private String html;
    private String hash;
    public LottoHtmlContext(String html, String hash)
    {
        this.html = html;
        this.hash = hash;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public boolean hasUpdate(String lastHash) {
        return hash.compareTo(lastHash) != 0;
    }
}
