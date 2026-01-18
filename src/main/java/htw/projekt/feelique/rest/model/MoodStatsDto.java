package htw.projekt.feelique.rest.model;

public class MoodStatsDto {
    private String mood;
    private long count;

    public MoodStatsDto() {
    }

    public MoodStatsDto(String mood, long count) {
        this.mood = mood;
        this.count = count;
    }

    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}