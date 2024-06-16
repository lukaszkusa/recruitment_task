package io.getint.recruitment_task;

class PropertyFileNotFoundException extends RuntimeException {
    public PropertyFileNotFoundException() {
        super("The app.properties file not found");
    }
}
