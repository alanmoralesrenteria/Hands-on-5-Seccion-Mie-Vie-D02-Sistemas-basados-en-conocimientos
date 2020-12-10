(import pricing_engine.*)
(deftemplate Order       (declare (from-class Order)))
(deftemplate OrderItem   (declare (from-class OrderItem)))
(deftemplate CatalogItem (declare (from-class CatalogItem)))
(deftemplate Customer    (declare (from-class Customer)))
(deftemplate CreditCard  (declare (from-class CreditCard)))



(defrule books-waterstone-promo "En la compra de Hammet + Summer + Ghost , obten un 20% de descuento." (Order (total ?total)) (OrderItem {partNumber == 1}) (OrderItem {partNumber == 2}) (OrderItem {partNumber == 5})
        => (add (new Offer "En la compra de Hammet + Summer + Ghost , obten un 20% de descuento." (* ?total 0.20))))

(defrule hsbc-promo "En las compras mayores a $5000, obten un %15 de descuento al pagar con tarjetas HSBC" ?o <- (Order (tarjeta "HSBC") (total ?total) )
    => (add (new Offer "En las compras mayores a $5000, obten un %15 de descuento al pagar con tarjetas HSBC" (* ?total 0.15))))

(defrule water-vales "En la compras mayores a 1000 de contado, obten el 100 pesos en vales por cada 1000 pesos de compra." ?o <-(Order (tarjeta "null") {total > 1000})
    =>(add (new Offer "En la compraas mayores a 1000 de contado, obten el 100 pesos en vales por cada 1000 pesos de compra." ?o.total "true" )))

(defrule 7%-book-item-discount "Obten un 7% de descuento en la compra de mas de 15 articulos identicos (mayoreo)." ?o<-(OrderItem {quantity >= 15} (price ?price) (description ?itemdescription))
    => (add (new Offer "Obten un 7% de descuento en la compra de mas de 15 articulos identicos(mayoreo)." ?itemdescription (* (* ?price 0.07) ?o.quantity) )))

(defrule Ghosts-promo "En la compra de Ghosts (1 por compra), obten un vale de 50 pesos en tu proxima compra." (OrderItem {partNumber == 5} {quantity == 1})
    => (add (new Offer "En la compra de Ghosts (1 por compra), obten un vale de 50 pesos en tu proxima compra." 50 )))