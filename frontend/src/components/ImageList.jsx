/* eslint-disable react/prop-types */
import { useState, useEffect } from 'react';
import LazyLoad from 'react-lazyload';
import '../App.css';

function ImageList({ images }) {
    const [activeTab, setActiveTab] = useState('');

    const typeNames = {
        regular: 'Images',
        thumbnail: 'Thumbnails',
        icon: 'Icons'
    };

    const groupedImages = Object.entries(images).reduce((acc, [imgSrc, type]) => {
        const adjustedType = typeNames[type] || type;
        if (!acc[adjustedType]) acc[adjustedType] = [];
        acc[adjustedType].push(imgSrc);
        return acc;
    }, {});

    useEffect(() => {
        const firstAvailableType = Object.keys(groupedImages).find(type => groupedImages[type].length > 0);
        setActiveTab(firstAvailableType || '');
    // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [images]);

    const handleTabClick = (type) => {        
        setActiveTab('');
        setTimeout(() => {
            setActiveTab(type);
        }, 10);
    };
    

    return (
        <div className="output">
            <div className="tabs">
                {Object.keys(groupedImages).map((type) => (
                    groupedImages[type].length > 0 && (
                        <button
                            key={type}
                            onClick={() => handleTabClick(type)}
                            className={`tab-button ${activeTab === type ? 'active' : ''}`}
                        >
                            {type} ({groupedImages[type].length})
                        </button>
                    )
                ))}
            </div>
            <div className="tab-content">
                {activeTab && groupedImages[activeTab] && groupedImages[activeTab].map((src, index) => (
                    <LazyLoad key={index} height={200} offset={300} once>
                        <img src={src} alt={activeTab} width="200" className="tab-image" />
                    </LazyLoad>
                ))}
            </div>
        </div>
    );
}

export default ImageList;
