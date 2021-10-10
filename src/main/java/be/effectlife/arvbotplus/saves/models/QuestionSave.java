package be.effectlife.arvbotplus.saves.models;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class QuestionSave {
    private static final Logger LOG = LoggerFactory.getLogger(QuestionSave.class);

    public static class QuestionSaveItem {
        String username;
        String timestamp;
        String question;
        boolean answered;

        public QuestionSaveItem(String username, String timestamp, String question, boolean answered) {
            this.username = username;
            this.timestamp = timestamp;
            this.question = question;
            this.answered = answered;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public String getQuestion() {
            return question;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public boolean isAnswered() {
            return answered;
        }

        public void setAnswered(boolean answered) {
            this.answered = answered;
        }
    }

    List<QuestionSaveItem> questions;

    public QuestionSave(List<QuestionSaveItem> questions) {
        this.questions = questions;
    }

    public List<QuestionSaveItem> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionSaveItem> questions) {
        this.questions = questions;
    }
}
