import sha1 from "js-sha1";

// Local strength score
export const calculatePasswordStrength = (password) => {
  let score = 0;
  if (password.length >= 8) score++;
  if (/[A-Z]/.test(password)) score++;
  if (/[a-z]/.test(password)) score++;
  if (/[0-9]/.test(password)) score++;
  if (/[^A-Za-z0-9]/.test(password)) score++;
  return score;
};

export const getStrengthLabel = (score) => {
  switch (score) {
    case 0:
    case 1:
      return { label: "Very Weak", color: "red" };
    case 2:
      return { label: "Weak", color: "orange" };
    case 3:
      return { label: "Medium", color: "yellow" };
    case 4:
      return { label: "Strong", color: "lightgreen" };
    case 5:
      return { label: "Very Strong", color: "green" };
    default:
      return { label: "", color: "" };
  }
};

// Optional HIBP check
export const checkHIBP = async (password) => {
  const hashed = sha1(password).toUpperCase();
  const prefix = hashed.slice(0, 5);
  const suffix = hashed.slice(5);

  try {
    const res = await fetch(`https://api.pwnedpasswords.com/range/${prefix}`);
    const data = await res.text();
    return data.includes(suffix); // true if breached
  } catch (err) {
    console.error("HIBP check failed:", err);
    return false;
  }
};
