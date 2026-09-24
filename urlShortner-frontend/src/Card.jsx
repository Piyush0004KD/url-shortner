import './Card.css';
import { useState } from 'react';

function Card() {

    const [url, setUrl] = useState('');
    const [shortUrl, setShortUrl] = useState('');

    async function shortenUrl() {

        try {

            const response = await fetch('http://localhost:8080/', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    originalUrl: url
                })
            });

            if (!response.ok) {
                throw new Error('Failed to shorten URL');
            }

            const data = await response.text();

            
            setShortUrl(data);

        } catch (error) {
            console.error(error);
        }
    }

    return (
        <>
            <div className="container">

                <h1>URL SHORTNER</h1>

                <div className="url-input">

                    <input
                        type="url"
                        placeholder="Paste the URL"
                        value={url}
                        onChange={(e) => setUrl(e.target.value)}
                    />

                    <button onClick={shortenUrl}>
                        Shorten
                    </button>

                    {shortUrl && (
                        <div>
                            <h3>Your Shortened URL</h3>

                            <a
                                href={shortUrl}
                                target="_blank"
                                rel="noreferrer"
                            >
                                {shortUrl}
                            </a>
                        </div>
                    )}

                </div>

            </div>
        </>
    );
}

export default Card;
