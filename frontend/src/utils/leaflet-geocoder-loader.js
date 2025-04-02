import L from 'leaflet';
import 'leaflet-control-geocoder';

if (typeof L.Control.Geocoder === 'undefined') {
  // Force manual assignment if Vite didn't register it
  L.Control.Geocoder = window.L.Control.Geocoder;
}

export default L;

