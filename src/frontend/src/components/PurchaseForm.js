import { useState } from "react";
import axios from "axios";

export default function PurchaseForm() {
    const [personalId, setPersonalId] = useState("");
    const [requestedAmount, setRequestedAmount] = useState("");
    const [paymentPeriodMonths, setPaymentPeriodMonths] = useState("");
    const [decision, setDecision] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError(null);

        try {
            const response = await axios.post("http://localhost:8080/api/purchase/apply", {
                personalId,
                requestedAmount: parseFloat(requestedAmount),
                paymentPeriodMonths: parseInt(paymentPeriodMonths, 10),
            });
            setDecision(response.data);
        } catch (err) {
            setError("Failed to fetch decision. Please try again.");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="container">
            <h2>Purchase Application</h2>
            <form onSubmit={handleSubmit}>
                <div>
                    <label>Personal ID:</label>
                    <input
                        type="text"
                        value={personalId}
                        onChange={(e) => setPersonalId(e.target.value)}
                        required
                    />
                </div>
                <div>
                    <label>Requested Amount (€):</label>
                    <input
                        type="number"
                        value={requestedAmount}
                        onChange={(e) => setRequestedAmount(e.target.value)}
                        required
                    />
                </div>
                <div>
                    <label>Payment Period (Months):</label>
                    <input
                        type="number"
                        value={paymentPeriodMonths}
                        onChange={(e) => setPaymentPeriodMonths(e.target.value)}
                        required
                    />
                </div>
                <button type="submit" disabled={loading}>
                    {loading ? "Submitting..." : "Apply"}
                </button>
            </form>
            {error && <p className="error">{error}</p>}
            {decision && (
                <div>
                    <h3>Decision:</h3>
                    <p>{decision.approved ? "✅ Approved" : "❌ Denied"}</p>
                    <p>Approved Amount: €{decision.approvedAmount}</p>
                </div>
            )}
        </div>
    );
}
