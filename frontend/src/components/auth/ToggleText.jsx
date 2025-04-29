const ToggleText = ({ isLogin, onToggle }) => {
  return (
    <p className="text-sm text-center text-gray-600">
      {isLogin ? "Don't have an account?" : "Already have an account?"}{" "}
      <button
        type="button"
        onClick={onToggle}
        className="text-brand font-semibold hover:underline"
      >
        {isLogin ? "Sign up" : "Login"}
      </button>
    </p>
  );
};

export default ToggleText;