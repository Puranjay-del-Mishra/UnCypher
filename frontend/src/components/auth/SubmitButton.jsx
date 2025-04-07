// File: components/auth/SubmitButton.jsx
import { Loader2 } from "lucide-react";

const SubmitButton = ({ loading, label }) => {
  return (
    <button
      type="submit"
      className="w-full bg-brand text-white py-2 rounded-lg flex items-center justify-center gap-2 hover:bg-blue-700 transition disabled:opacity-50"
      disabled={loading}
    >
      {loading && <Loader2 className="animate-spin w-4 h-4" />}
      {loading ? "Please wait..." : label}
    </button>
  );
};

export default SubmitButton;