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
            const response = await axios.post("/api/purchase/apply", {
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
        <div className="container d-flex justify-content-center align-items-center min-vh-100">
            <div className="card shadow-lg p-4" style={{ width: "400px" }}>
                <h2 className="text-center mb-3">Purchase Application</h2>
                <form onSubmit={handleSubmit}>
                    <div className="mb-3">
                        <label htmlFor="personalId" className="form-label">
                            Personal ID
                        </label>
                        <input
                            type="text"
                            className="form-control"
                            id="personalId"
                            value={personalId}
                            onChange={(e) => setPersonalId(e.target.value)}
                            required
                        />
                    </div>
                    <div className="mb-3">
                        <label htmlFor="requestedAmount" className="form-label">
                            Requested Amount (€)
                        </label>
                        <input
                            type="number"
                            className="form-control"
                            id="requestedAmount"
                            value={requestedAmount}
                            onChange={(e) => setRequestedAmount(e.target.value)}
                            required
                        />
                    </div>
                    <div className="mb-3">
                        <label htmlFor="paymentPeriodMonths" className="form-label">
                            Payment Period (Months)
                        </label>
                        <input
                            type="number"
                            className="form-control"
                            id="paymentPeriodMonths"
                            value={paymentPeriodMonths}
                            onChange={(e) => setPaymentPeriodMonths(e.target.value)}
                            required
                        />
                    </div>
                    <button type="submit" className="btn btn-primary w-100" disabled={loading}>
                        {loading ? "Submitting..." : "Apply"}
                    </button>
                </form>

                {error && <p className="text-danger mt-3 text-center">{error}</p>}

                {decision && (
                    <div className="alert mt-3 text-center">
                        <p className="fw-bold">Decision:</p>
                        <p>{decision.approved ? "✅ Approved" : "❌ Denied"}</p>
                        <p>Approved Amount: €{decision.approvedAmount}</p>
                    </div>
                )}
            </div>
        </div>
    );
}
