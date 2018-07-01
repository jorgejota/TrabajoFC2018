package crawly.controllers;

public class Response {
		private String status;
		private double porcentage[];
		public Response() {
			
		}
		public Response(String status, double porcentage[]) {
			super();
			this.status = status;
			this.porcentage = porcentage;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public double[] getPorcentage() {
			return porcentage;
		}
		public void setPorcentage(double[] porcentage) {
			this.porcentage = porcentage;
		} 
}
