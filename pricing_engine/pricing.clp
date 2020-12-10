;; First define templates for the model classes so we can use them
;; in our pricing rules. This doesn't create any model objects --
;; it just tells Jess to examine the classes and set up templates
;; using their properties

(import pricing_engine.*)
(deftemplate Order       (declare (from-class Order)))
(deftemplate OrderItem   (declare (from-class OrderItem)))
(deftemplate CatalogItem (declare (from-class CatalogItem)))
(deftemplate Customer    (declare (from-class Customer)))
(deftemplate CreditCard  (declare (from-class CreditCard)))

;; Now define the pricing rules themselves. Each rule matches a set
;; of conditions and then creates an Offer object to represent a
;; bonus of some kind given to a customer. The rules assume that
;; there will be just one Order, its OrderItems, and its Customer in 
;; working memory, along with all the CatalogItems.


(defrule iphone-24-meses-sin-intereses "Obten 24 meses sin intereses al comprar un iPhone 11 pro al pagar con tarjetas banamex" (OrderItem {(partNumber == 111) && (name == "Banamex")})
    => (add (new Offer "Obten 24 meses sin intereses al comprar un iPhone 11 pro al pagar con tarjetas banamex" "Banamex" )))


(defrule samsung-12-meses-sin-intereses "Obten 12 meses sin intereses al comprar un Samsung note 11 al pagar con tarjetas Liverpool VISA" (OrderItem {(partNumber == 222) && (name == "Liverpool VISA")})
    => (add (new Offer "Obten 12 meses sin intereses al comprar un Samsung note 11 al pagar con tarjetas Liverpool VISA" "Liverpool VISA" )))


(defrule Mac-Vales "En la compra de una MacBook Air obeten 100 pesos en vales por cada 1000 pesos de compra" (OrderItem {partNumber == 333 } (price ?price))
    => (add (new Offer "En la compra de una MacBook Air obeten 100 pesos en vales por cada 1000 pesos de compra" ?price "true" )))


(defrule 10%-volume-discount "Give a 10% discount to everybody who spends more than $100." ?o <- (Order {total > 100})
    => (add (new Offer "10% volume discount" (/ ?o.total 10))))

(defrule 25%-multi-item-discount "Give a 25% discount on items the customer buys three or more of." (OrderItem {quantity >= 3} (price ?price))
    => (add (new Offer "25% multi-item discount" (/ ?price 4))))

(defrule free-cd-rw-disks
    "If somebody buys a CD writer, send them a free sample of CD-RW
    disks, catalog number 782321; but only if they're a repeat customer.
    We use a regular expression to match the CD writer's description."
    (CatalogItem (partNumber ?partNumber) (description /CD Writer/))
    (CatalogItem (partNumber 782321) (price ?price))
    (OrderItem (partNumber ?partNumber))
    (Customer {orderCount > 1})
    =>	
    (add (new Offer "Free CD-RW disks" ?price)))





(defrule 20%-amazon-discount "Obten un 20% de descuento en la compra de mas de $10000 en Amazon" ?o <- (Order {total > 100000})
    => (add (new Offer "Obten un 20% de descuento en la compra de mas de $10000 en Amazon" (/ ?o.total 20))))

(defrule 5%-amazon-item-discount "Obten un 5% de descuento en la compra de mas de 5 articulos identicos (mayoreo)." (OrderItem {quantity >= 5} (price ?price) (description ?itemdescription))
    => (add (new Offer "Obten un 5% de descuento en la compra de mas de 5 articulos identicos(mayoreo)." ?itemdescription (/ ?price 5))))


(defrule test-discount "Descuento para pruebas" (Order) 
    => 
(add(new offer "El usuario de desarrollo obtiene 100% de descuento, para poder realizar pruebas.")))

