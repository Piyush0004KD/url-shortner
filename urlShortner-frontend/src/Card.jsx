import './Card.css';

function Card(){

  return(
    <>
    <div className="container">
    <h1>URL SHORTNER</h1>

    <div className="url-input">
        <input type="text" placeholder="Paste the Url" />
        <a href="">Shortened-Url</a>
    </div>
</div> 
    </>

  );


}

export default Card
