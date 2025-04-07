
import logo from "../assets/uncypher_logo.png";

const Logo = ({ size = "h-8 w-auto" }) => {
  return (
    <div className={`${size} w-fit`}>
      <img
        src={logo}
        alt="UnCypher Logo"
        className="h-full w-auto object-contain"
      />
    </div>
  );
};

export default Logo;
