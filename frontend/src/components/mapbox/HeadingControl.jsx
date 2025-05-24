import { useEffect } from 'react';

const HeadingControl = ({ map }) => {
  useEffect(() => {
    if (!map || typeof window.DeviceOrientationEvent === 'undefined') return;

    const handleOrientation = (event) => {
      const heading = event.alpha;
      if (typeof heading === 'number') {
        map.setBearing(heading);
      }
    };

    window.addEventListener('deviceorientationabsolute', handleOrientation, true);

    return () => {
      window.removeEventListener('deviceorientationabsolute', handleOrientation, true);
    };
  }, [map]);

  return null;
};

export default HeadingControl;
