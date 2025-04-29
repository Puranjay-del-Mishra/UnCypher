import { useState, useEffect } from "react";
import { motion } from "framer-motion";
import { calculatePasswordStrength, getStrengthLabel, checkHIBP } from "../utils/passwordUtils";

const strengthColors = {
  0: "#e11d48", // red
  1: "#e11d48",
  2: "#f97316", // orange
  3: "#eab308", // yellow
  4: "#4ade80", // light green
  5: "#22c55e", // green
};

const PasswordInput = ({ onChange }) => {
  const [password, setPassword] = useState("");
  const [score, setScore] = useState(0);
  const [breached, setBreached] = useState(false);

  useEffect(() => {
    const newScore = calculatePasswordStrength(password);
    setScore(newScore);

    const timeout = setTimeout(() => {
      if (password.length > 5) {
        checkHIBP(password).then((found) => {
          setBreached(found);
          onChange?.(password, newScore, found);
        });
      } else {
        setBreached(false);
        onChange?.(password, newScore, false);
      }
    }, 500);

    return () => clearTimeout(timeout);
  }, [password]);

  const { label } = getStrengthLabel(score);

  return (
    <div className="flex flex-col gap-2">
      <input
        type="password"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
        placeholder="Enter a strong password"
        className="input-field"
      />

      <div className="h-2 w-full bg-gray-200 rounded">
        <motion.div
          initial={{ width: 0 }}
          animate={{ width: `${(score / 5) * 100}%` }}
          transition={{ duration: 0.3 }}
          className="h-full rounded"
          style={{ backgroundColor: strengthColors[score] }}
        />
      </div>

      <p className="text-sm font-medium" style={{ color: strengthColors[score] }}>
        {label}
      </p>

      {breached && (
        <p className="text-sm text-red-600 font-medium">
          ⚠️ This password can be breached. Try again!
        </p>
      )}
    </div>
  );
};

export default PasswordInput;

