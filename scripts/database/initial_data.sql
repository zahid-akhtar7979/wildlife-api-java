-- Insert into articles
INSERT INTO articles (
    id, title, content, excerpt, published, featured, category,
    views, images, videos, publish_date, author_id
)
VALUES
-- Article 1
(1,
 'Orangutans of Borneo: Protecting the Last Forest Gardeners',
 '<p>Discover the critical situation facing Bornean orangutans and learn how conservation efforts are working to save these incredible forest gardeners.</p>',
 'Discover the critical situation facing Bornean orangutans and learn how conservation efforts are working to save these incredible forest gardeners.',
 true,
 false,
 'Primates',
 654,
 '[{"url":"https://images.unsplash.com/photo-1544985361-b420d7a77043?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=800&h=600&q=80","caption":"Orangutan mother with her baby in Borneo rainforest"}]',
 '[]',
 '2024-01-05',
 1),

-- Article 2
(2,
 'Bengal Tigers: Guardians of the Sundarbans',
 '<p>Explore the unique ecosystem of the Sundarbans and the magnificent Bengal tigers that call this mangrove forest home.</p>',
 'Explore the unique ecosystem of the Sundarbans and the magnificent Bengal tigers that call this mangrove forest home.',
 true,
 false,
 'Big Cats',
 892,
 '[{"url":"https://images.unsplash.com/photo-1551969014-7d2c4cddf0b6?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1000&q=80","caption":"Bengal tiger in Sundarbans mangrove forest"}]',
 '[]',
 '2024-01-03',
 2),

-- Article 3
(3,
 'African Elephants: The Gentle Giants of the Savanna',
 '<p>Learn about the complex social structures of African elephants and the conservation challenges they face in the modern world.</p>',
 'Learn about the complex social structures of African elephants and the conservation challenges they face in the modern world.',
 true,
 false,
 'Large Mammals',
 567,
 '[{"url":"https://images.unsplash.com/photo-1564760055775-d63b17a55c44?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1000&q=80","caption":"African elephant herd in savanna"}]',
 '[]',
 '2024-01-01',
 3);

-- Insert tags for article 1
INSERT INTO article_tags (article_id, tag) VALUES
(1, 'Primates'),
(1, 'Orangutans'),
(1, 'Conservation');

-- Insert tags for article 2
INSERT INTO article_tags (article_id, tag) VALUES
(2, 'Big Cats'),
(2, 'Tigers'),
(2, 'Conservation');

-- Insert tags for article 3
INSERT INTO article_tags (article_id, tag) VALUES
(3, 'Large Mammals'),
(3, 'Elephants'),
(3, 'Conservation');
