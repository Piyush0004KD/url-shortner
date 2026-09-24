import { useEffect, useState } from 'react';
import './UrlList.css';

function UrlList() {

    const [urls, setUrls] = useState([]);

    async function fetchUrls() {

        try {
            const response = await fetch(
                'http://localhost:8080/?page=0&size=10'
            );

            if (!response.ok) {
                throw new Error('Failed to fetch URLs');
            }

            const data = await response.json();

            setUrls(data.content);

        } catch (error) {
            console.error(error);
        }
    }

    useEffect(() => {
        fetchUrls();
    }, []);

    return (
        <div className="url-list">

            <h2>Your Shortened URLs</h2>

            {urls.map((url) => (
                <div className="url-item" key={url.shortUrl}>

                    <p className="original-url">{url.longUrl}</p>

                    <a
                        href={`http://localhost:8080/${url.shortUrl}`}
                        target="_blank"
                        rel="noopener noreferrer"
                    >
                        {url.shortUrl}
                    </a>

                </div>
            ))}

        </div>
    );
}

export default UrlList;
