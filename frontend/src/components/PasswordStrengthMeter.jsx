import zxcvbn from "zxcvbn";

const PasswordStrengthMeter = ({ password }) => {
  const { score, feedback } = zxcvbn(password);

  const strength = [
    "Very Weak",
    "Weak",
    "Fair",
    "Good",
    "Strong"
  ];

  const getColor = () => {
    return ["bg-red-500", "bg-orange-500", "bg-yellow-500", "bg-blue-500", "bg-green-500"][score];
  };

  return (
    <div className="mt-2">
      <div className="w-full h-2 bg-gray-300 rounded">
        <div className={`h-2 rounded ${getColor()}`} style={{ width: `${(score + 1) * 20}%` }} />
      </div>
      <p className="text-sm text-gray-600 mt-1">
        Strength: <strong>{strength[score]}</strong>
        {feedback.warning && <span className="ml-2 text-red-500">- {feedback.warning}</span>}
      </p>
    </div>
  );
};

export default PasswordStrengthMeter;