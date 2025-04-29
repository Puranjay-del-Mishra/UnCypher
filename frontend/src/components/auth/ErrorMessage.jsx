const ErrorMessage = ({ message }) => {
  return message ? (
    <p className="text-sm text-red-600 text-center">{message}</p>
  ) : null;
};

export default ErrorMessage;
