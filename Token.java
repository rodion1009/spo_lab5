public class Token {
    private String text;
    private String type;

    public Token(String text, String type) {
        this.text = text;
        this.type = type;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "[type: " + type + ", text: " + text + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Token token = (Token) obj;
        return getType().equals(token.getType()) && getText().equals(token.getText());
    }
}
