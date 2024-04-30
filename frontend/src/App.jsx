import { useState } from 'react';
import './App.css';
import ImageSearchForm from './components/ImageSearchForm';
import ImageList from './components/ImageList';

function App() {
  const [url, setUrl] = useState('');
  const [images, setImages] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [allowIcons, setAllowIcons] = useState(false);
  const [allowThumbnails, setAllowThumbnails] = useState(false);
  const [searchDepth, setSearchDepth] = useState(0);

  const fetchImages = async () => {
    if (!url.trim()) {
      setError('Please enter a valid URL.');
      return;
    }
  
    setError('');
    setLoading(true);
  
    try {
      const params = new URLSearchParams({
        url: url,
        allowIcons,
        allowThumbnails,
        searchDepth
      });
  
      const response = await fetch('/main', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: params
      });
  
      if (!response.ok) {
        throw new Error(`HTTP error! Status: ${response.status}`);
      }
  
      const imgUrls = await response.json();
      setImages(imgUrls);
    } catch (error) {
      setError(`Failed to fetch images: ${error.message}`);
    } finally {
      setLoading(false);
    }
  };
  

  return (
    <div className="App">
      <h1>Image Finder</h1>
      <ImageSearchForm
        url={url}
        setUrl={setUrl}
        fetchImages={fetchImages}
        loading={loading}
        error={error}
        allowIcons={allowIcons}
        setAllowIcons={setAllowIcons}
        allowThumbnails={allowThumbnails}
        setAllowThumbnails={setAllowThumbnails}
        searchDepth={searchDepth}
        setSearchDepth={setSearchDepth}
      />
      <ImageList images={images} />
    </div>
  );
}

export default App;
