ALTER TABLE kluchs
ADD CONSTRAINT FK_author
FOREIGN KEY (author_id)
REFERENCES user (id);